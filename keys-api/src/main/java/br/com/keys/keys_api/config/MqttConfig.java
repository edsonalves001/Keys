package br.com.keys.keys_api.config;

import br.com.keys.keys_api.exception.MqttResposta;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttConfig {

    @Value("${mqtt.username:}")
    private String username;

    @Value("${mqtt.password:}")
    private String password;

    @Value("${mqtt.broker:tcp://localhost:1883}")
    private String broker;

    @Value("${mqtt.client-id:keys-backend}")
    private String clientId;

    @Value("${mqtt.topico.resposta:keys/resposta}")
    private String topicoResposta;

    @Value("${mqtt.enabled:true}")
    private boolean mqttEnabled;
    private MqttClient client;
    private boolean inscrito = false;
    private final ApplicationEventPublisher eventPublisher;

    // Primeira tentativa ao iniciar o Spring Boot
    @PostConstruct
    public void iniciar() {
        if (mqttEnabled) {
            conectar();
        } else {
            System.out.println("MQTT desativado nas configurações.");
        }
    }

    // Tenta conectar novamente a cada 15 segundos
    @Scheduled(fixedDelay = 15000, initialDelay = 15000)
    public synchronized void conectar() {
        if (!mqttEnabled) {
            return;
        }

        try {
            // Cria o cliente apenas uma vez
            if (client == null) {
                client = new MqttClient(broker, clientId);
                client.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable cause) {
                        inscrito = false;
                        System.out.println("Conexão MQTT perdida!");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        System.out.println();
                        System.out.println("========== MENSAGEM MQTT RECEBIDA ==========");

                        System.out.println("Tópico: " + topic);
                        System.out.println("Mensagem: " + new String(message.getPayload()));
                        System.out.println("QoS: " + message.getQos());

                        System.out.println("============================================");
                        String resposta = new String(message.getPayload());
                        eventPublisher.publishEvent(new MqttResposta(topic, resposta));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Mensagem enviada!");
                    }
                });
            }

            // Se já estiver conectado, não tenta conectar novamente
            if (client.isConnected()) {
                if (!inscrito) {
                    client.subscribe(topicoResposta, 1);
                    inscrito = true;
                }
                return;
            }
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            client.connect(options);
            client.subscribe(topicoResposta, 1);
            inscrito = true;
            System.out.println("Conectado ao broker MQTT!");
        } catch (MqttException e) {
            inscrito = false;
            System.out.println("Broker MQTT indisponível. " + "O sistema continuará funcionando normalmente.");
            System.out.println("Nova tentativa de conexão em 15 segundos.");
        }
    }

    public MqttClient getClient() {
        return client;
    }

    @PreDestroy
    public void desconectar() {
        try {
            if (client != null) {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            }
        } catch (MqttException e) {
            System.out.println("Erro ao desconectar do MQTT.");
        }
    }
}
