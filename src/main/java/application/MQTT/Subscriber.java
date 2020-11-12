package application.MQTT;
import java.lang.Thread;
import java.util.Properties;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.Marker;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class Subscriber{
	
	private static final Logger logger = LogManager.getLogger(Subscriber.class.getName());
    public static void main ( String[] args ){
    	
    	InputStream resources = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
    	Properties properties = new Properties();
    	try {
    		properties.load(resources);
    	}catch(IOException e) {
    		logger.error("Properties could not be read", e);
    	}
    	
    	String topic = properties.getProperty("topic");
    	String subscriberId = UUID.randomUUID().toString();
    	MqttClient subscriber = null;
    	try {
    		subscriber = new MqttClient(properties.getProperty("mqttbroker_url"),subscriberId);
    		logger.info("Subscriber created");
    	}catch(MqttException e){
    		logger.error(e.getMessage() + "Exception ReasonCode:"+ e.getReasonCode());
    	}
    	
    	MqttConnectOptions options = new MqttConnectOptions();
    	options.setCleanSession(Boolean.TRUE);
    	options.setConnectionTimeout(8);
    	options.setAutomaticReconnect(Boolean.TRUE);
    	
    	try {
    	subscriber.connect(options);
    	}catch(MqttException e) {
    		logger.error("Connection to broker failed", e);
    	}
    	
    	try {
    	subscriber.subscribe(topic, 0);
    	}catch(MqttException e) {
    		logger.error("Could not subscribe to topic", e);
    	}
    	
    	System.out.println("Works");
    	/*try {
            subscriber.subscribe(topicP, () -> {
                byte[] payload = m.getPayload();
                String[] recievedValue = new String(payload).split("/");
            });
        } catch (MqttException e) {
            logger.error("Cannot subscribe");
        }*/
    	
    }
}
