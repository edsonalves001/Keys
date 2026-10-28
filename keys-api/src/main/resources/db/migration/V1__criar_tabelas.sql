-- V1__criar_tabelas.sql

-- 1. Empresa (sem FK, pode ser primeira)
CREATE TABLE tb_empresa (
    cd_cnpj_empresa VARCHAR(255) PRIMARY KEY,
    nm_empresa      VARCHAR(255) NOT NULL
);

-- 2. Sala (sem FK)
CREATE TABLE tb_sala (
    cd_sala                 SERIAL PRIMARY KEY,
    nm_sala                 VARCHAR(80) NOT NULL,
    ds_sala                 VARCHAR(255),
    ic_disponibilidade_sala VARCHAR(255) NOT NULL
);

-- 3. Usuário (FK pra empresa + auto-referência)
CREATE TABLE tb_usuario (
    cd_usuario             VARCHAR(255) PRIMARY KEY,
    nm_usuario             VARCHAR(100) NOT NULL,
    nm_email_usuario       VARCHAR(100) NOT NULL UNIQUE,
    nm_senha_usuario       VARCHAR(255) NOT NULL,
    nm_link_imagem_usuario VARCHAR(255),
    cd_admin               VARCHAR(255),
    cd_empresa             VARCHAR(255),
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (cd_empresa)
        REFERENCES tb_empresa (cd_cnpj_empresa),
    CONSTRAINT fk_usuario_admin FOREIGN KEY (cd_admin)
        REFERENCES tb_usuario (cd_usuario)
);

-- 4. Reserva (FK pra usuario + sala)
CREATE TABLE tb_reserva (
    cd_reserva        SERIAL PRIMARY KEY,
    dh_inicio_reserva TIMESTAMP(6) NOT NULL,
    dh_fim_reserva    TIMESTAMP(6) NOT NULL,
    ds_reserva        VARCHAR(255),
    nm_status_sala    VARCHAR(255) NOT NULL,
    status_reserva    VARCHAR(255) NOT NULL,
    cd_usuario        VARCHAR(255),
    cd_sala           INTEGER,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (cd_usuario)
        REFERENCES tb_usuario (cd_usuario),
    CONSTRAINT fk_reserva_sala FOREIGN KEY (cd_sala)
        REFERENCES tb_sala (cd_sala),
    CONSTRAINT ck_reserva_nm_status_sala CHECK (
        nm_status_sala IN ('DISPONIVEL','RESERVARDA','INDISPONIVEL','EM_US0')
    ),
    CONSTRAINT ck_reserva_status CHECK (
        status_reserva IN ('EM_USO','FINALIZADA')
    )
);