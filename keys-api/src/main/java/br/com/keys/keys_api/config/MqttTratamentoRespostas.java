package br.com.keys.keys_api.config;

import br.com.keys.keys_api.exception.MqttResposta;
import br.com.keys.keys_api.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//Essa classe serve para enviar a resposta que o esp8266 enviou para o mqtt
//e que foi repassada para o Spring Boot
@Component
@RequiredArgsConstructor
public class MqttTratamentoRespostas {


        private final ReservaService reservaService;

        @EventListener
        public void receberResposta(MqttResposta event) {
            String resposta = event.resposta();

            if (resposta.equals("INICIADO: 2")) {
                reservaService.retornoEfetivacao(resposta);
            }else if(resposta.equals("ENCERRAR: 2")){
                reservaService.retornoEfetivacao(resposta);
            }

            if (resposta.equals("INICIADO: 3")) {
                reservaService.retornoEfetivacao(resposta);
            }else if(resposta.equals("ENCERRAR: 3")){
                reservaService.retornoEfetivacao(resposta);
            }
        }
}
