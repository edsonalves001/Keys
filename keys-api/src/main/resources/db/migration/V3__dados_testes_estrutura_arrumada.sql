-- Arrumando relacionamento da tabela de salas com a empresa:

ALTER TABLE tb_sala
ADD COLUMN cd_empresa VARCHAR(255);

ALTER TABLE tb_sala ADD CONSTRAINT fk_sala_empresa FOREIGN KEY (cd_empresa) REFERENCES tb_empresa (cd_cnpj_empresa);

ALTER TABLE tb_sala ALTER COLUMN cd_empresa SET NOT NULL;

-- Removendo coluna extra tabela (maldita ia que add campo extra):
ALTER TABLE tb_reserva DROP COLUMN nm_status_sala;

-- V3__popular_dados_teste.sql

-- ============================================
-- 1. SALAS (tb_sala não tem FK de empresa — salas globais)
-- ============================================
INSERT INTO tb_sala (nm_sala, ds_sala, ic_disponibilidade_sala, cd_empresa) VALUES
('Sala 1', 'Sala pequena com 4 lugares',        'DISPONIVEL', '11.222.333/0001-81'),
('Sala 2', 'Sala média com 8 lugares',          'DISPONIVEL', '11.222.333/0001-81'),
('Sala 3', 'Sala grande com 12 lugares',        'INDISPONIVEL', '11.222.333/0001-81'),
('Sala 4', 'Auditório com 40 lugares',          'DISPONIVEL', '45.678.901/0001-23'),
('Sala 5', 'Sala de reunião com 6 lugares',     'DISPONIVEL', '45.678.901/0001-23');

-- ============================================
-- 2. USUÁRIOS COMUNS (subordinados dos admins)
-- Senha de todos: Senha@123
-- Hash BCrypt: $2a$12$8/grhtA2JROSiuwMLcR5uegu53xlVhC3esXTEqbJN4C20TNeLGyni
-- ============================================

-- Subordinados do Rogerinho (admin '123ab23' — empresa 11.222.333/0001-81)
INSERT INTO tb_usuario (
    cd_usuario, nm_usuario, nm_email_usuario, nm_senha_usuario,
    nm_link_imagem_usuario, cd_admin, cd_empresa
) VALUES
('123b',  'Ronaldo', 'ronaldo@gmail.com',
 '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO',
 '/uploads/usuarios/foto-padrao.png', '123ab23', '11.222.333/0001-81'),

('123b2', 'Pedro', 'pedro@hotmail.com',
 '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO',
 '/uploads/usuarios/foto-padrao.png', '123ab23', '11.222.333/0001-81'),

('123b3', 'Maria', 'maria@gmail.com',
 '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO',
 '/uploads/usuarios/foto-padrao.png', '123ab23', '11.222.333/0001-81'),

-- Subordinados do Pedrinho (admin '123ab24' — empresa 45.678.901/0001-23)
('123b4', 'Joana', 'joana@gmail.com',
 '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO',
 '/uploads/usuarios/foto-padrao.png', '123ab24', '45.678.901/0001-23'),

('123b5', 'Carlos', 'carlos@gmail.com',
 '$2a$12$yisLb.CP1ONOHx6Dn0XGFe4/I6GG/HQOXfvLMxINgu76Ze7Izj9EO',
 '/uploads/usuarios/foto-padrao.png', '123ab24', '45.678.901/0001-23');

-- ============================================
-- 3. RESERVAS
-- CHECKs atuais:
--   status_reserva IN ('EM_USO','FINALIZADA')
-- ============================================
INSERT INTO tb_reserva (
    dh_inicio_reserva, dh_fim_reserva, ds_reserva,
    status_reserva, cd_usuario, cd_sala
) VALUES
('2026-10-30 09:00:00', '2026-10-30 11:00:00', 'Reunião de planejamento',   'EM_USO',     '123b',  1),
('2026-10-28 14:00:00', '2026-10-28 16:00:00', 'Treinamento de onboarding', 'FINALIZADA', '123b2', 2),
('2026-10-31 10:00:00', '2026-10-31 12:00:00', 'Manutenção da sala',        'EM_USO',     '123b3', 3),
('2026-10-30 13:00:00', '2026-10-30 15:00:00', 'Apresentação para cliente', 'EM_USO',     '123b4', 4),
('2026-10-27 08:00:00', '2026-10-27 10:00:00', 'Daily da equipe',           'FINALIZADA', '123b5', 5),
('2026-11-02 15:00:00', '2026-11-02 17:00:00', 'Retrospectiva do sprint',   'EM_USO',     '123b',  5);