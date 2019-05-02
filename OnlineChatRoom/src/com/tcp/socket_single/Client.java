package com.tcp.socket_single;

import java.io.*;
import java.net.Socket;

/**
 *
 * Created by Jiaxin Li on 4/17/19.
 *
 * Description: The client class implements the Client behaviors including socket client, double-direction message
 * tube, and logout functionality.
 */

public class Client {

    public static void main(String[] args) throws IOException {

        Socket client = new Socket("localhost", 14426);
        System.out.println("Server connected");

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

        String msg;
        while((msg = console.readLine()) != null) {
            if (msg != null && msg.length() > 0) {
                writer.println(msg);
                System.out.println(reader.readLine());

                if (msg.split(" ")[0].equals("logout")) {
                    client.close();
                    System.out.println("Client: connection closed");
                    break;
                }

            }

        }


    }



}
