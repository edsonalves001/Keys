package br.com.keys.keys_api.config;

import br.com.keys.keys_api.exception.MqttResposta;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

//Classe responsavel pela configuração do MQTT
//

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = false) //precisa explicitamente tá ativado no application properties
public class MqttConfig {
    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topico.resposta}")
    private String topicoResposta;

    private MqttClient client;
    private final ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void conectar() {
        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Conexão MQTT perdida!");
                }

                //Metodo que capta toda resposta enviada do broker
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String resposta = new String(message.getPayload());
                    eventPublisher.publishEvent(new MqttResposta(topic, resposta));
                }

                //Metodo que envia a mensagem para o broken
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Mensagem enviada!");
                }
            });
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            client.connect(options);
            client.subscribe(topicoResposta, 1);
            System.out.println("Conectado ao broker MQTT!");
        } catch (MqttException e) {
            throw new IllegalStateException(
                    "Não foi possível conectar ao MQTT", e.getCause()
            );
        }
    }
    public MqttClient getClient() {
        return client;
    }

    @PreDestroy
    public void desconectar() {
        try {

            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}