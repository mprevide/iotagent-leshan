package org.cpqd.iotagent.kafka;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cpqd.iotagent.TenancyManager;
import org.json.JSONObject;

import java.util.*;
import java.util.function.Function;

public class KafkaHandler implements Runnable {

    public static String TENANCY_MANAGER_SUBJECT = "dojot.tenancy";
    public static String TENANCY_MANAGER_URL = "http://auth:5000";
    public static String DATA_BROKER_DEVICE_ENDPOINT = "dojot.device-manager.device";
    public static String DATA_BROKER_DATA_ENDPOINT = "device-data";
    public static String DATA_BROKER_MANAGER = "http://localhost:80/topic/";
    public static String BOOTSTRAP_SERVER = "localhost:9092";


    private Producer<Long, String> producer;
    private String deviceDataTopic;
    private String deviceManagerTopic;
    private final KafkaConsumer<String, String> consumer;
    private final Map<String, Function<String, String>> EventCallbacks = new HashMap<String, Function<String, String>>();

    public KafkaHandler()  {

        // Consumer proprierties
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVER);
        props.put("group.id", "cpqd");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        // Build Consumer
        this.consumer = new KafkaConsumer<>(props);


        // Producer proprierties
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Build Producer
        this.producer = new KafkaProducer<>(props);

        // Get Topics
        deviceDataTopic = GetTopic("admin", DATA_BROKER_DATA_ENDPOINT);
        deviceManagerTopic = GetTopic("admin", DATA_BROKER_DEVICE_ENDPOINT);
    }



    // RegisterEventCallback

    public void sendMessage(String Message, String topic) {
        long time = System.currentTimeMillis();
        final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, time, Message);
        try {
            RecordMetadata metadata = producer.send(record).get();
            System.out.printf("sent record(key=%s value=%s) meta(partition=%d, offset=%d)\n",
                    record.key(), record.value(), metadata.partition(), metadata.offset());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    public void UpdateAttr(String service, String deviceId, JsonElement attrs){
        JsonObject metadata = new JsonObject();
        metadata.addProperty("deviceid", deviceId);
        metadata.addProperty("tenant", service);
        JsonObject message = new JsonObject();
        message.add("metadata", metadata);
        message.add("attrs", attrs);

        //TODO(jsiloto) get topic based on service
        sendMessage(message.toString(), deviceDataTopic);
    }


    public static String GetTopic(String service, String endpoint) {
        try {
            String token = TenancyManager.GetJwtToken(service);
            HttpResponse<JsonNode> response = Unirest.get(DATA_BROKER_MANAGER + endpoint)
                    .header("Authorization", "Bearer " + token).asJson();
            return response.getBody().getObject().getString("topic");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public void RegisterCallback(String event, Function<String, String> callback) {
        EventCallbacks.put(event, callback);
    }

    private void TreatMessage(String message) {
        try {

            System.out.println(message);
            JSONObject kafkaEvent = new JSONObject(message);
            String event = kafkaEvent.get("event").toString();
            String data = kafkaEvent.get("data").toString();

            if (EventCallbacks.containsKey(event)) {
                EventCallbacks.get(event).apply(data);
            } else {
                System.out.println(event + " : " + data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {

            // Todo(jsiloto) Separate this in a method for multi-tenant support
            List<String> topics = new LinkedList<String>();
            topics.add(deviceManagerTopic);

            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    TreatMessage(record.value());
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }



}
