package views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import dto.response.ResponseDto;

public class ClientRecive extends Thread{

	private Socket socket;
	private Gson gson;
	
	public ClientRecive(Socket socket) {
		this.socket = socket;
		gson = new Gson();
	}
	
	@Override
	public void run() {
		
		try {
			InputStream inputStream = socket.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			while(true) {
				String responseJson = bufferedReader.readLine();
				responseMapping(responseJson);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void responseMapping(String responseJson) {
		ResponseDto<?> responseDto = gson.fromJson(responseJson, ResponseDto.class);
		
		switch (responseDto.getResource()) {
			case "usernameCheckIsBlank":
			case "usernameCheckIsDuplicate":
				JOptionPane.showMessageDialog(null, (String) responseDto.getBody(), "접속오류", JOptionPane.WARNING_MESSAGE);			
				break;
			case "usernameCheckSuccessfully":
				ClientApplication.getInstance()
								.getMainCard()
								.show(ClientApplication.getInstance().getMainPanel(), "roomListPanel");
				break;
			case "refreshRoomList":
				refreshRoomList((List<Map<String, String>>) responseDto.getBody());
				break;
			case "createRoomSuccessfully":
				ClientApplication.getInstance()
								.getMainCard()
								.show(ClientApplication.getInstance().getMainPanel(), "roomPanel");
				break;
			case "refreshUsernameList":
				refreshUsernameList((List<String>) responseDto.getBody());
				break;
			default:
				break;
		}
		
	}
	
	private void refreshRoomList(List<Map<String, String>> roomList) {
		ClientApplication.getInstance().getRoomNameListModel().clear();
		ClientApplication.getInstance().setRoomInfoList(roomList);
		for(Map<String, String> roomInfo : roomList) {
			ClientApplication.getInstance().getRoomNameListModel().addElement(roomInfo.get("roomName"));
		}
	}
	
	private void refreshUsernameList(List<String> usernameList) {
		ClientApplication.getInstance().getUsernameListModel().clear();
		ClientApplication.getInstance().getUsernameListModel().addAll(usernameList);
	}
}
