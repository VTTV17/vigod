package utilities.get_port;

import java.io.IOException;
import java.net.ServerSocket;

public class FreePort {
    public static int get() {
        // Init new socket with a random port
        try (ServerSocket socket = new ServerSocket(0)) {

            // Get local port
            int port = socket.getLocalPort();

            // Close socket
            socket.close();

            // Return free port
            return port;
        } catch (IOException e) {
            throw new RuntimeException("Failed to find a free port", e);
        }
    }
}
