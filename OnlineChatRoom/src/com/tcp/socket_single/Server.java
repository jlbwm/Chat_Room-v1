package com.tcp.socket_single;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiaxin Li on 4/17/19.
 *
 * Description: The Server class implements all the server functionality including socket server, double-direction message
 * tube, user authentication, logout, send and relevant error detection.
 */

//1. create server 2. write data 3. read data
public class Server {

    private static List<User> users = new ArrayList<>();
    private static boolean isLogin = false;
    private static String currentUser;


    public static void main(String[] args) throws IOException {

        System.out.println("Retrieving account info ......");
        initialAccount("users.txt");


        ServerSocket server = new ServerSocket(14426);

        PrintWriter writer;
        BufferedReader reader;
        Socket client;

        while(true) {
            System.out.println("Link available......");

            client = server.accept();
            System.out.println("New client accepted");

            //Send to Client
            writer = new PrintWriter(client.getOutputStream(), true);

            //Read from Client
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String command;
            while ((command = reader.readLine()) != null) {

                String[] commands = command.split(" ");

                if (commands[0].equals("login")) {
                    if (isLogin == false) {
                        if (commands.length == 3) {

                            int checkValue = checkAccount(commands[1], commands[2]);
                            String status = getAuthentication(commands[1], checkValue);
                            if (checkValue == 1) {
                                isLogin = true;
                                currentUser = commands[1];
                                System.out.println(currentUser + " login");
                            }
                            writer.println(status);
                            continue;

                        } else {
                            writer.println("Server: Error, empty field");
                            continue;
                        }
                    } else {
                        writer.println("Server: You have already logged in");
                        continue;
                    }
                }

                if (commands[0].equals("send")) {
                    if (isLogin) {
                        if (commands.length >= 2) {
                            String msg = command.substring(5);
                            writer.println(currentUser + ": " + msg);
                            System.out.println(currentUser + ": " + msg);
                            continue;
                        } else {
                            writer.println("Server: Empty message.");
                            continue;
                        }
                    } else {
                        writer.println("Server: Denied. Please login first.");
                        continue;
                    }
                }

                if (commands[0].equals("newuser")) {
                    if (!isLogin) {
                        if (commands.length == 3) {
                            String uid = commands[1];
                            String pwd = commands[2];

                            if (uid.length() >= 32) {
                                writer.println("Server: Error. User ID should less than 32 characters");
                                continue;
                            }

                            if (pwd.length() < 4 || pwd.length() > 8) {
                                writer.println("Server: Error. Password should between 4 and 8 characters");
                                continue;
                            }

                            int checkValue = checkAccount(uid, pwd);
                            if (checkValue == -1) {

                                createAndWriteNewUser(uid, pwd, "users.txt");
                                writer.println("Server: Sign up success. You can log in now");
                                System.out.println(uid + " created");
                                continue;

                            } else {
                                writer.println("Server: User ID is occupied");
                                continue;
                            }

                        } else {
                            writer.println("Server: Error, empty field");
                            continue;
                        }

                    } else {
                        writer.println("Server: You have already logged in");
                        continue;
                    }
                }

                if (commands[0].equals("logout")) {
                    if (isLogin) {
                        writer.println("Server: " + currentUser + " left.");
                        System.out.println(currentUser + " logout.");
                        isLogin = false;
                        currentUser = null;
                        client.close();
                        System.out.println("Current client exit");
                        break;

                    } else {
                        writer.println("Server: Denied. Please login first.");
                        continue;
                    }
                }
                writer.println("Server: Invalid input. Please try again.");

            }

        }
    }

    /**
     *
     * @param filePath: the Account file
     */
    private static void initialAccount(String filePath) {
        File file = new File(filePath);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;

            while ((line = br.readLine()) != null) {

                String[] strs = line.split(",");
                users.add(new User(strs[0], strs[1]));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param userID
     * @param pwd
     * @return 1 for valid user
     *         0 for invalid pwd | uid has already been created
     *         -1 for invalid userID | uid can be created
     */
    private static int checkAccount(String userID, String pwd) {
        for(User user : users) {
            if(user.getUserID().equals(userID)){
                return user.getPwd().equals(pwd) ? 1 : 0;
            }
        }
        return -1;
    }

    /**
     *
     * @param uid
     * @param status 1 for valid user, 0 for invalid pwd, -1 for invalid userID
     * @return hint message
     */
    private static String getAuthentication(String uid, int status) {
        String result = "System Error(getAuthentication)";

        String validUser = "Server: " + uid + " joins.";
        String invalidPWD = "Server: Password not correct, please try again.";
        String invalidUser = "Server: Invalid user ID, please try again.";

        if (status == 1) {
            result = validUser;
        }

        if (status == 0) {
            result = invalidPWD;
        }
        if (status == -1) {
            result = invalidUser;
        }

        return result;
    }


    /**
     * Store new user to account file and the users list
     * @param uid
     * @param pwd
     * @param filePath
     */
    private static void createAndWriteNewUser(String uid, String pwd, String filePath) {

        User newUser = new User(uid, pwd);
        users.add(newUser);

        String newUserString = uid + "," + pwd;

        File file = new File(filePath);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(newUserString);
            bw.newLine();
            bw.flush();
            bw.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
