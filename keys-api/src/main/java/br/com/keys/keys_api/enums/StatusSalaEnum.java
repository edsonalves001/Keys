package br.com.keys.keys_api.enums;

public enum StatusSalaEnum {
    DISPONIVEL, //A Sala está disponivel (tanto para reserva, quanto para uso)
    RESERVADA, //A Sala está indisponivel, pois já foi reservada, porém não está em uso
    INDISPONIVEL, //A Sala está indisponivel para uso
    EM_USO //A sala está em uso, logo está indisponivel
}
