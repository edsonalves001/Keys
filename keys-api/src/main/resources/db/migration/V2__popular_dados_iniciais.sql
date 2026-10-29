-- V2__popular_dados_iniciais.sql

-- 1. Empresas
INSERT INTO tb_empresa (cd_cnpj_empresa, nm_empresa) VALUES
('11.222.333/0001-81', 'Keys Tecnologia LTDA'),
('45.678.901/0001-23', 'Espaços Coworking SA');

-- 2. Admin da primeira empresa (Rogerinho, sem chefe)
-- Senha em texto puro: Senha@123
INSERT INTO tb_usuario (
    cd_usuario,
    nm_usuario,
    nm_email_usuario,
    nm_senha_usuario,
    nm_link_imagem_usuario,
    cd_admin,
    cd_empresa
) VALUES (
    '123ab23',
    'Rogerinho',
    'rogerinho@gmail.com',
    '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO', -- Senha@123
    '/uploads/usuarios/foto-padrao.png',
    NULL,
    '11.222.333/0001-81'
);

-- 3. Admin da segunda empresa (Pedrinho, sem chefe)
-- Senha em texto puro: Senha@123
INSERT INTO tb_usuario (
    cd_usuario,
    nm_usuario,
    nm_email_usuario,
    nm_senha_usuario,
    nm_link_imagem_usuario,
    cd_admin,
    cd_empresa
) VALUES (
    '123ab24',
    'Pedrinho',
    'pedrinho@hotmail.com',
    '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO', -- Senha@123
    '/uploads/usuarios/foto-padrao.png',
    NULL,
    '45.678.901/0001-23'
);