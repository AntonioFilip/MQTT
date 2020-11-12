package application.MQTT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class EngineSensor implements Runnable{

	private static final Logger logger = LogManager.getLogger(EngineSensor.class.getName());
	private MqttClient client;
	private String clientId;
	
	public EngineSensor(MqttClient client,String clientId) {
		this.client = client;
		this.clientId = clientId;
	}
	
	public void run() {
		
		if(!client.isConnected()) {
			logger.info("Aborted, no clients connected");
			System.exit(0);
		}
				
		Properties properties = new Properties();
		InputStream resources = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
		
		try {
			properties.load(resources);
		}catch(IOException e) {
			logger.error(e);
		}
		
		String topic = properties.getProperty("topic");
		
		MqttMessage m = readSensor();
		m.setQos(2);
		m.setRetained(Boolean.FALSE);
		
		try {
		client.publish(topic, m);
		logger.info("Message {} published on topic {}", m, topic);
		}catch(MqttException e) {
			logger.error("Error whilst publishing {} / {}",e.getMessage(),e.getReasonCode());
		}
		
		
	}
	
	private MqttMessage readSensor() {
		double tempInt = RandomUtils.nextDouble(65, 90);;
		String temp = String.format("%.2f", tempInt);
		double volumetricEffciencyInt = RandomUtils.nextDouble(1 , 1.8);
		String volumetricEffciency = String.format("%.2f", volumetricEffciencyInt);
		byte [] payload = String.format("%s %s %s", "Volvo  " ,"  Temp = " + temp ,"  VolumetricEfficiency = " + volumetricEffciency).getBytes();
		//byte [] payload = new byte[0];
		MqttMessage m = new MqttMessage(payload);
		
		return m;
	}
	
}
