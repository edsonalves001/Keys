package br.com.keys.keys_api.service;

import br.com.keys.keys_api.config.MqttConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service

public class MqttService {
    private final MqttConfig mqttConfig;

    @Value("${mqtt.topico.comando}")
    private String topicoComando;

    public MqttService(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public boolean getStatusConexao() {
        return mqttConfig.getClient() != null
                && mqttConfig.getClient().isConnected();
    }
    private void publicar(String mensagem) {
        MqttClient client = mqttConfig.getClient();

        if (client == null || !client.isConnected()) {
            System.out.println("MQTT indisponível. Comando não enviado: " + mensagem);
            return;
        }

        try {
            MqttMessage mqttMessage = new MqttMessage(
                    mensagem.getBytes(StandardCharsets.UTF_8)
            );

            mqttMessage.setQos(1);

            client.publish(topicoComando, mqttMessage);

            System.out.println("MQTT publicado com sucesso!");
            System.out.println("Tópico: " + topicoComando);
            System.out.println("Mensagem: " + mensagem);

        } catch (MqttException e) {
            System.err.println("Erro ao publicar MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void iniciarReserva(Integer cdReserva) {
        System.out.println("========== INICIAR RESERVA ==========");
        System.out.println("Método iniciarReserva() foi chamado!");
        System.out.println("Código da reserva: " + cdReserva);

        System.out.println("Chamando o método publicar()...");
        publicar("INICIAR: " + cdReserva);

        System.out.println("Método publicar() retornou!");
        publicar("INICIAR: " + cdReserva);
    }

    public void finalizarReserva(Integer cdReserva) {
        publicar("ENCERRAR: " + cdReserva);
    }

    public void informarErro() {
        publicar("Nenhum resultado positivo");
    }
}
