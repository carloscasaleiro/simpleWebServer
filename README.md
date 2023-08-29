# Simple Web Server

The SimpleWebServer class listens on port 8080 for incoming client connections. It handles GET requests by dispatching the request to process and respond with the requested files, such as HTML and image files. The HttpMedia class assists in determining the content type of requested files for proper HTTP response headers.

The server reads the requested resource from the client's HTTP request, processes it, and responds with appropriate status codes and headers. It supports serving HTML files and images like JPG, PNG, and ICO files. The server responds to requests with the requested file's content, along with the appropriate Content-Type headers.

Project made during the Academia de Código bootcamp between May -> Aug 2023. www.academiadecodigo.org
<p></p>

Run and try on browser:

http://localhost:8080/index.html

http://localhost:8080/logo.png


http://localhost:8080/404.html
