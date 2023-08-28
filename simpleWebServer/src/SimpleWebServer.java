import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Web Server
 */
public class SimpleWebServer {

    public static final int PORT = 8080;

    private void listen() {

        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            serve(serverSocket);

        } catch (IOException e) {

            System.out.println(e.getMessage());
            System.exit(1);

        }
    }

    private void serve(ServerSocket serverSocket) {

        while (true) {

            try {

                Socket clientSocket = serverSocket.accept();
                dispatch(clientSocket);

            } catch (IOException e) {

                System.out.println(e.getMessage());

            }
        }
    }

    private void dispatch(Socket clientSocket) {

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            String requestHeaders = fetchRequestHeaders(in);

            String request = requestHeaders.split("\n")[0]; // request is first line of header
            String httpVerb = request.split(" ")[0]; // verb is the first word of request
            String resource = request.split(" ").length > 1 ? request.split(" ")[1] : null; // second word of request

            String filePath = getPathForResource(resource);

            if (resource == null) {
                reply(out, "HTTP/1.0 400 Bad Request\r\n");
                clientSocket.close();
                return;
            }


            if (httpVerb.equals("GET")) {

                File file = new File(filePath);

                if (file.exists() && !file.isDirectory()) {

                    reply(out, "HTTP/1.0 200 Document Follows\r\n");

                } else {

                    reply(out, "HTTP/1.0 404 Not Found\r\n");
                    filePath = "Resources/404.html";
                    file = new File(filePath);

                }

                reply(out, HttpMedia.contentType(filePath));
                reply(out, "Content-Length: " + file.length() + "\r\n\r\n");

                streamFile(out, file);

            } else {

                reply(out, "HTTP/1.0 405 Method Not Allowed\r\n" +
                        "Allow: GET\r\n");

            }

            clientSocket.close();

        } catch (IOException e) {

            System.out.println(e.getMessage());

        }
    }

    private String fetchRequestHeaders(BufferedReader in) throws IOException {

        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = in.readLine()) != null && !line.isEmpty()) {

            builder.append(line).append("\n");

        }

        return builder.toString();
    }

    private String getPathForResource(String resource) {

        String filePath = resource;

        if (filePath.equals("/favicon.ico")) {
            filePath = "Resources/favicon.ico"; // Path to favicon file
        } else {
            Pattern pattern = Pattern.compile("(\\.[^.]+)$"); // regex for file extension
            Matcher matcher = pattern.matcher(filePath);

            if (!matcher.find()) {
                filePath += "/index.html";
            }

            filePath = "Resources/" + filePath;
        }

        return filePath;
    }


    private void streamFile(DataOutputStream out, File file) throws IOException {

        byte[] buffer = new byte[1024];
        FileInputStream in = new FileInputStream(file);

        int numBytes;
        while ((numBytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, numBytes);
        }

        in.close();
    }

    private void reply(DataOutputStream out, String response) throws IOException {
        out.writeBytes(response);
    }


    public static void main(String[] args) {

        SimpleWebServer simpleWebServer = new SimpleWebServer();
        simpleWebServer.listen();
    }

    static class HttpMedia {

        public static boolean isImage(String file) {

            switch (getExtension(file)) {
                case "jpg":
                case "png":
                case "ico":
                    return true;
                default:
                    return false;
            }
        }

        public static String getExtension(String file) {
            return file.substring(file.lastIndexOf(".") + 1);
        }

        public static String contentType(String file) {

            if (isImage(file)) {
                return "Content-Type: image/" + getExtension(file) + "\r\n";
            }

            return "Content-Type: text/html; charset=UTF-8\r\n";
        }
    }
}
