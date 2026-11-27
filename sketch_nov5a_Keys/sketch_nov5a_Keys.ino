#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <Servo.h>

Servo motor1, motor2;
const int Minion1 = 5;             // D1
const int Minion2 = 4;             // D2
const int RedLedRedemption = 12;   // D6
const int AlertBuzzer = 13;        // D7
const int GreenGoblinLed = 14;     // D5
const char* ssid = NOMEDOWIFI;
const char* wifi_password = SENHADOWIFI;
const char* mqtt_server = IPDOCOMPUTADOR;
const int mqtt_port = 1883;
const char* mqtt_user = "keys_iot";
const char* mqtt_password = "123456";
const char* topicoComando = "keys/springboot/01/esp8266";
const char* topicoResposta = "keys/esp8266/01/springboot";

WiFiClient espClient;
PubSubClient client(espClient);
unsigned long tempoEstado = 0;
unsigned long ultimaTentativaWiFi = 0;
unsigned long ultimaTentativaMQTT = 0;
unsigned long ultimaTentativaResposta = 0;


enum EstadoMotor {
    MOTOR_OCIOSO,
    SINAL_VERDE_LIGADO,
    SINAL_VERDE_DESLIGADO,
    AGUARDANDO_ABERTO
};

EstadoMotor estadoMotor = MOTOR_OCIOSO;
Servo* motorAtual = nullptr;
int quantidadePiscadas = 0;
const unsigned long TEMPO_PISCADA = 150;
const unsigned long TEMPO_ABERTO = 10000;
const char* respostaOperacao = nullptr;
const char* respostaPendente = nullptr;

enum EstadoNegacao {
    NEGACAO_OCIOSA,
    NEGACAO_TOM_1,
    NEGACAO_TOM_2,
    NEGACAO_FINAL
};

EstadoNegacao estadoNegacao = NEGACAO_OCIOSA;
void conectarWiFi();
void conectarMQTT();
void callback(char* topic, byte* payload, unsigned int length);
void iniciarMotor(Servo& motor, const char* resposta);
void atualizarMotor();
void iniciarNegacao();
void atualizarNegacao();
void enviarResposta(const char* resposta);
void publicarRespostaPendente();


void setup() {
    Serial.begin(115200);
    pinMode(RedLedRedemption, OUTPUT);
    pinMode(GreenGoblinLed, OUTPUT);
    pinMode(AlertBuzzer, OUTPUT);
    digitalWrite(RedLedRedemption, LOW);
    digitalWrite(GreenGoblinLed, LOW);
    noTone(AlertBuzzer);
    motor1.attach(Minion1);
    motor2.attach(Minion2);
    conectarWiFi();
    client.setServer(mqtt_server, mqtt_port);
    client.setCallback(callback);
    client.setKeepAlive(30);
    Serial.println("Sistema KEYS inicializado!");
}

void loop() {
    unsigned long agora = millis();
    if (WiFi.status() != WL_CONNECTED) {
        if (agora - ultimaTentativaWiFi >= 10000) {
            ultimaTentativaWiFi = agora;
            Serial.println("Tentando reconectar ao Wi-Fi...");
            conectarWiFi();
        }
    }

    if (WiFi.status() == WL_CONNECTED) {
        if (!client.connected()) {
            if (agora - ultimaTentativaMQTT >= 3000) {
                ultimaTentativaMQTT = agora;
                conectarMQTT();
            }
        } else {
            client.loop();
        }

    }

    atualizarMotor();
    atualizarNegacao();
    publicarRespostaPendente();
    yield();

}

void conectarWiFi() {
    WiFi.mode(WIFI_STA);
    WiFi.persistent(false);
    WiFi.setAutoReconnect(true);
    Serial.println();
    Serial.print("Conectando ao Wi-Fi: ");
    Serial.println(ssid);
    WiFi.begin(ssid, wifi_password);
    ultimaTentativaWiFi = millis();
}

void conectarMQTT() {
    if (WiFi.status() != WL_CONNECTED) {
        return;
    }

    Serial.println();
    Serial.println("Tentando conectar ao Mosquitto...");

    if (client.connect("keys-esp8266-01", mqtt_user, mqtt_password)) {
        Serial.println("MQTT conectado!");
        bool inscrito = client.subscribe(topicoComando, 1);
        if (inscrito) {
            Serial.println("Inscricao realizada com sucesso!");
            Serial.print("Topico: ");
            Serial.println(topicoComando);
        } else {
            Serial.println("Falha ao realizar inscricao!");
        }
    } else {
        Serial.print("Falha MQTT. Estado: ");
        Serial.println(client.state());
    }
}

void iniciarMotor(Servo& motor, const char* resposta) {
    if (estadoMotor != MOTOR_OCIOSO ||
        estadoNegacao != NEGACAO_OCIOSA) {
        Serial.println("Operacao em andamento. Comando ignorado!");
        return;
    }

    motorAtual = &motor;
    respostaOperacao = resposta;
    quantidadePiscadas = 0;
    Serial.println();
    Serial.println("Validacao positiva!");
    Serial.println("Iniciando sinalizacao...");
    digitalWrite(GreenGoblinLed, HIGH);
    tone(AlertBuzzer, 3200);
    tempoEstado = millis();
    estadoMotor = SINAL_VERDE_LIGADO;

}

void atualizarMotor() {
    unsigned long agora = millis();

    switch (estadoMotor) {
        case SINAL_VERDE_LIGADO:
            if (agora - tempoEstado >= TEMPO_PISCADA) {
                digitalWrite(GreenGoblinLed, LOW);
                noTone(AlertBuzzer);
                tempoEstado = agora;
                estadoMotor = SINAL_VERDE_DESLIGADO;
            }
            break;

        case SINAL_VERDE_DESLIGADO:
            if (agora - tempoEstado >= TEMPO_PISCADA) {
                quantidadePiscadas++;

                if (quantidadePiscadas < 3) {
                    digitalWrite(GreenGoblinLed, HIGH);
                    tone(AlertBuzzer, 3200);
                    tempoEstado = agora;
                    estadoMotor = SINAL_VERDE_LIGADO;

                } else {
                    Serial.println("Abrindo trava...");
                    motorAtual->write(0);
                    Serial.println("Trava aberta!");
                    Serial.println("Aguardando 10 segundos...");
                    tempoEstado = agora;
                    estadoMotor = AGUARDANDO_ABERTO;

                }
            }
            break;
        case AGUARDANDO_ABERTO:
            if (agora - tempoEstado >= TEMPO_ABERTO) {
                Serial.println("Fechando trava...");
                motorAtual->write(180);
                digitalWrite(GreenGoblinLed, LOW);
                noTone(AlertBuzzer);
                Serial.println("Operacao concluida!");
                enviarResposta(respostaOperacao);
                motorAtual = nullptr;
                respostaOperacao = nullptr;
                estadoMotor = MOTOR_OCIOSO;
            }

            break;

        case MOTOR_OCIOSO:
            break;

    }
}

void iniciarNegacao() {
    if (estadoMotor != MOTOR_OCIOSO ||
        estadoNegacao != NEGACAO_OCIOSA) {
        Serial.println("Outra operacao esta em andamento!");
        return;
    }

    Serial.println("Nenhum resultado positivo!");
    digitalWrite(RedLedRedemption, HIGH);
    tone(AlertBuzzer, 3200);
    tempoEstado = millis();
    estadoNegacao = NEGACAO_TOM_1;

}

void atualizarNegacao() {
    unsigned long agora = millis();

    switch (estadoNegacao) {
        case NEGACAO_TOM_1:
            if (agora - tempoEstado >= 100) {
                tone(AlertBuzzer, 1000);
                tempoEstado = agora;
                estadoNegacao = NEGACAO_TOM_2;
            }
            break;

        case NEGACAO_TOM_2:
            if (agora - tempoEstado >= 150) {
                noTone(AlertBuzzer);
                tempoEstado = agora;
                estadoNegacao = NEGACAO_FINAL;
            }
            break;

        case NEGACAO_FINAL:
            if (agora - tempoEstado >= 500) {
                digitalWrite(RedLedRedemption, LOW);
                enviarResposta("RESPOSTA_ESP8266_OK");
                estadoNegacao = NEGACAO_OCIOSA;

            }
            break;
        case NEGACAO_OCIOSA:
            break;

    }

}

void enviarResposta(const char* resposta) {
    respostaPendente = resposta;
    ultimaTentativaResposta = 0;

}

void publicarRespostaPendente() {

    if (respostaPendente == nullptr) {
        return;
    }

    if (!client.connected()) {
        return;
    }

    unsigned long agora = millis();

    if (agora - ultimaTentativaResposta < 1000) {
        return;
    }

    ultimaTentativaResposta = agora;

    bool enviado = client.publish(
        topicoResposta,
        respostaPendente,
      false
    );
    if (enviado) {
        Serial.print("Resposta enviada: ");
        Serial.println(respostaPendente);
        respostaPendente = nullptr;
    } else {
        Serial.println("Falha ao publicar resposta MQTT!");
    }
}

void callback(char* topic, byte* payload, unsigned int length) {
    Serial.print("Topico: ");
    Serial.println(topic);

    if (String(topic) != topicoComando) {
        return;
    }

    String mensagem = "";
    for (unsigned int i = 0; i < length; i++) {
        mensagem += (char)payload[i];
    }

    mensagem.trim();
    Serial.print("Mensagem recebida: ");
    Serial.println(mensagem);

    if (mensagem == "Nenhum resultado positivo") {
        iniciarNegacao();
        return;
    }

    if (mensagem == "INICIAR: 2") {
        iniciarMotor(motor2, "INICIADO: 2");
        return;
    }

    if (mensagem == "ENCERRAR: 2") {
        iniciarMotor(motor2, "ENCERRAR: 2");
        return;
    }

    if (mensagem == "INICIAR: 3") {
        iniciarMotor(motor1, "INICIADO: 3");
        return;
    }
    if (mensagem == "ENCERRAR: 3") {
        iniciarMotor(motor1, "ENCERRAR: 3");
        return;
    }
    Serial.println("COMANDO NAO RECONHECIDO!");
}
