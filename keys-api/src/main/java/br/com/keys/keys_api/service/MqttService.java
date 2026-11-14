package br.com.keys.keys_api.service;

import br.com.keys.keys_api.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
@ConditionalOnProperty(
        name = "mqtt.enabled",
        havingValue = "true",
        matchIfMissing = false
)

public class MqttService {
    private final MqttConfig mqttConfig;

    @Value("${mqtt.topico.comando}")
    private String topicoComando;

    public MqttService(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    private void publicar(String mensagem) {
        try {
            MqttClient client = mqttConfig.getClient();
            if (client == null || !client.isConnected()) {
                throw new IllegalStateException("MQTT não está conectado");
            }

            MqttMessage mqttMessage = new MqttMessage(mensagem.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(1);
            client.publish(topicoComando, mqttMessage);
            System.out.println("Comando enviado: " + mensagem);

        } catch (MqttException e) {
            throw new RuntimeException(
                    "Erro ao enviar comando MQTT", e
            );
        }
    }

    public void iniciarReserva(Integer cdReserva) {
        publicar("INICIAR: " + cdReserva);
    }

    public void finalizarReserva(Integer cdReserva) {
        publicar("ENCERRAR: " + cdReserva);
    }

    public void informarErro() {
        publicar("Nenhum resultado positivo");
    }
}
