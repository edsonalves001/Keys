package br.com.keys.keys_api.service;

import br.com.keys.keys_api.config.MqttConfig;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final MqttConfig mqttConfig;

    @Value("${mqtt.topico.comando}")
    private String topicoComando;

    public MqttService(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public void iniciarReserva(Integer CdReserva) {
        try {
            MqttClient client = mqttConfig.getClient();
            if (client == null || !client.isConnected()) {
                throw new IllegalStateException("MQTT não está conectado");
            }

            String SalaIniciarReserva = "INICIAR: " + CdReserva.toString();
            MqttMessage mqttMessage = new MqttMessage(SalaIniciarReserva.getBytes());
            mqttMessage.setQos(1);
            client.publish(topicoComando, mqttMessage);
            System.out.println("Comando enviado: " + mqttMessage);

        } catch (MqttException e) {
            throw new RuntimeException("Erro ao enviar comando MQTT", e);
        }
    }
    public void finalizarReserva(Integer CdReserva){
        try {
            MqttClient client = mqttConfig.getClient();
            if (client == null || !client.isConnected()) {
                throw new IllegalStateException("MQTT não está conectado");
            }
            String SalaFinalizar = "ENCERRAR: " + CdReserva.toString();
            MqttMessage mqttMessage = new MqttMessage(SalaFinalizar.getBytes());
            mqttMessage.setQos(1);
            client.publish(topicoComando, mqttMessage);
            System.out.println("Comando enviado: " + mqttMessage);

        } catch (MqttException e) {
            throw new RuntimeException("Erro ao enviar comando MQTT", e);
        }
    }
    public void InformarErro() {
        try {
            MqttClient client = mqttConfig.getClient();
            if (client == null || !client.isConnected()) {
                throw new IllegalStateException("MQTT não está conectado");
            }
            String erro  = "Nenhum resultado positivo";
            MqttMessage mqttMessage = new MqttMessage(erro.getBytes());
            mqttMessage.setQos(1);
            client.publish(topicoComando, mqttMessage);
            System.out.println("Comando enviado: " + mqttMessage);
        } catch (MqttException e) {
            throw new RuntimeException("Erro ao enviar comando MQTT", e);
        }
    }
}