package Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


public class ConnectToServer {

    private final static Logger logger = LogManager.getLogger(ConnectToServer.class.getName());

    public Session createServerSession(String user, String host, String password) {
        Session session = null;
        try {
            JSch jsch = new JSch();
            logger.debug("Attempting connection to " + user + "@" + host);
            session = jsch.getSession(user, host, 22);
            Properties prop = new Properties();
            prop.setProperty("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.setPassword(password);
            session.connect();
            logger.debug("Connected to " + user + "@" + host);

        } catch (Exception e) {
            logger.debug("NOT Connected to " + user + "@" + host + ". Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return session;
    }

    public Boolean isSessionConnected(Session ssession) {
        return ssession.isConnected();
    }

    public void disconnectToServer(Session ssession) {
        if (ssession != null) {
            ssession.disconnect();
        }
        logger.debug("Disconnected successfully");
    }

//	public String getServerLogsBase64(Session ssession, String linuxCommand) throws JSchException, IOException {
//		String encodedBase64 = null;
//		System.out.println("linuxCommand : " + linuxCommand);
//		Channel channel = ssession.openChannel("exec");
//		((ChannelExec) channel).setCommand(linuxCommand);
//		channel.setInputStream(null);
//		((ChannelExec) channel).setErrStream(System.err);
//
//		InputStream in = channel.getInputStream();
//		channel.connect();
//
//		try {
//			byte[] bytes = in.readAllBytes();
//			encodedBase64 = new String(Base64.getEncoder().encode(bytes));
//			channel.disconnect();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "data:text/txt;base64," + encodedBase64;
//	}

    public void createLogFile(Session ssession,String filePath, String linuxCommand) throws JSchException, IOException {
        Channel channel = ssession.openChannel("exec");
        ((ChannelExec) channel).setCommand(linuxCommand);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        channel.disconnect();

    }

    public String readServerLogs(Session ssession, String linuxCommand)
            throws JSchException, IOException {
        String serverResponse = null;
        Channel channel = ssession.openChannel("exec");
        ((ChannelExec) channel).setCommand(linuxCommand);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();
        serverResponse = IOUtils.toString(in, StandardCharsets.UTF_8);
        channel.disconnect();

        return serverResponse;
    }

    public static boolean copyFileFromServer(Session ssession, String source, String destination)
            throws JSchException, IOException {
        boolean flag=false;
        try
        {
            Channel channel = ssession.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(source, destination);
            sftpChannel.exit();
            flag=true;

        }catch (JSchException e)
        {
            e.printStackTrace();
        }
        catch (SftpException e)
        {
            e.printStackTrace();
        }
        System.out.println("Done !!");

        return flag;
    }




}