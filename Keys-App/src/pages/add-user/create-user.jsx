import { useState } from "react";
import Menu from "../../components/full-menu";
import "./create-user.css";
import baixarexcel from "../../assets/baixar-excel.png";
import randomProfile from "../../assets/random-profile.png";
import deleteIcon from "../../assets/delete.png";
import editIcon from "../../assets/edit-reservation.png";
import { api } from "../../api/api.js";

const form_vazio = {
  cd_usuario: "",
  nm_usuario: "",
  nm_email_usuario: "",
  nm_senha_usuario: "",
  nm_confirmar_senha: "",
};

const CreateUser = () => {
  // array de users em rascunho (ainda não foram para o banco)
  const [users, setUsers] = useState([]);

  // form de adicionar
  const [form, setForm] = useState(form_vazio);

  // modal de editar: null = fechado; objeto = cópia do user sendo editado
  const [editando, setEditando] = useState(null);

  // modais de feedback
  const [infoModal, setInfoModal] = useState("");
  const [ativarModalSucesso, setAtivarModalSucesso] = useState(false);
  const [ativarModalErro, setAtivarModalErro] = useState(false);

  const [enviando, setEnviando] = useState(false);

  // helpers de modal (um fecha o outro)
  function mostrarSucesso(msg) {
    setInfoModal(msg);
    setAtivarModalSucesso(true);
    setAtivarModalErro(false);
  }
  function mostrarErro(msg) {
    setInfoModal(msg);
    setAtivarModalErro(true);
    setAtivarModalSucesso(false);
  }

  // FORM DE ADICIONAR
  function onChangeForm(e) {
    const { name, value } = e.target;
    setForm((ant) => ({ ...ant, [name]: value }));
  }

  // mesma validação do backend (espelhada para UX)
  function validarUser(u) {
    if (!u.cd_usuario.trim()) return "Código do usuário é obrigatório.";
    if (!u.nm_usuario.trim()) return "Nome do usuário é obrigatório.";
    if (!u.nm_email_usuario.trim()) return "Email é obrigatório.";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(u.nm_email_usuario))
      return "Formato de email inválido.";
    const regexSenha =
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$/;
    if (!regexSenha.test(u.nm_senha_usuario))
      return "Senha fraca! Mínimo 8 caracteres com maiúscula, minúscula, dígito e caractere especial.";
    if (u.nm_senha_usuario !== u.nm_confirmar_senha)
      return "Senhas não batem!";
    // código único dentro do próprio rascunho
    if (users.some((x) => x.cd_usuario === u.cd_usuario))
      return "Já existe um usuário com esse código na lista.";
    // email único dentro do próprio rascunho
    if (users.some((x) => x.nm_email_usuario === u.nm_email_usuario))
      return "Já existe um usuário com esse email na lista.";
    return null;
  }

  function handleAdicionar(e) {
    e.preventDefault();
    const erro = validarUser(form);
    if (erro) {
      mostrarErro(erro);
      return;
    }
    setUsers((ant) => [...ant, { ...form }]);
    setForm(form_vazio); // limpa o form
  }

  // EDITAR (modal, cópia)
  function abrirEdicao(user) {
    setEditando({ ...user }); // cópia, não referência
  }

  function onChangeEdicao(e) {
    const { name, value } = e.target;
    setEditando((ant) => ({ ...ant, [name]: value }));
  }

  function handleSalvarEdicao() {
    // valida só os campos editáveis (sem checar duplicidade contra si mesmo)
    if (!editando.nm_usuario.trim()) {
      mostrarErro("Nome do usuário é obrigatório.");
      return;
    }
    if (!editando.nm_email_usuario.trim()) {
      mostrarErro("Email é obrigatório.");
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editando.nm_email_usuario)) {
      mostrarErro("Formato de email inválido.");
      return;
    }
    // email não pode duplicar outro user da lista
    if (
      users.some(
        (x) =>
          x.cd_usuario !== editando.cd_usuario &&
          x.nm_email_usuario === editando.nm_email_usuario
      )
    ) {
      mostrarErro("Já existe outro usuário com esse email na lista.");
      return;
    }

    setUsers((ant) =>
      ant.map((u) => (u.cd_usuario === editando.cd_usuario ? { ...editando } : u))
    );
    setEditando(null);
  }

  // delete (visual)
  function handleRemover(cd_usuario) {
    setUsers((ant) => ant.filter((u) => u.cd_usuario !== cd_usuario));
  }

  // Criar um user por vez no banco  (foreach)
  async function handleConfirmar() {
    if (users.length === 0) {
      mostrarErro("Não há usuários a serem criados.");
      return;
    }
    setEnviando(true);

    let erros = 0; //pra exibir modal dps
    console.log("Adicionando: ", users)

    //Pegado localstorage
    const token = localStorage.getItem('token');
    console.log(token)

    for (const u of users) {
      try {

        await api.post("/auth/criarUser",
          {
            cd_usuario: u.cd_usuario,
            nm_usuario: u.nm_usuario,
            nm_email_usuario: u.nm_email_usuario,
            nm_senha_usuario: u.nm_senha_usuario,
            nm_confirmar_senha: u.nm_confirmar_senha,
            cd_cnpj_empresa: "generico", //o endpoint de criaruser acaba nem usando e eu tenho que enviar pq reutilizei o mesmo do criar adm que usa!
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            }
          }
        );
      } catch (e) {
        erros += 1
        console.log(erros)
        setInfoModal("Verifique suas credenciais!")
        setAtivarModalErro(true)
        setEnviando(false)
        return
      }
    }

    if (erros == 0) {
      setInfoModal("Usuário adicionados com sucesso!!")
      setAtivarModalSucesso(true)
      setUsers([])
      setEnviando(false)
    }
    erros = 0;
    return
  }

  console.log("Users atuais a serem add: ", users)

  return (
    <>
      <Menu />

      <main className="create-user-main">
        <section className="form-add-user">
          <h1>Adicionar Usuário</h1>
          <form onSubmit={handleAdicionar}>
            <input
              type="text"
              name="cd_usuario"
              placeholder="Digite o código do usuário"
              value={form.cd_usuario}
              onChange={onChangeForm}
            />
            <input
              type="text"
              name="nm_usuario"
              placeholder="Digite o nome do usuário"
              value={form.nm_usuario}
              onChange={onChangeForm}
            />
            <input
              type="email"
              name="nm_email_usuario"
              placeholder="Digite o email do usuário"
              value={form.nm_email_usuario}
              onChange={onChangeForm}
            />
            <input
              type="password"
              name="nm_senha_usuario"
              placeholder="Digite a senha"
              value={form.nm_senha_usuario}
              onChange={onChangeForm}
            />
            <input
              type="password"
              name="nm_confirmar_senha"
              placeholder="Confirme a senha"
              value={form.nm_confirmar_senha}
              onChange={onChangeForm}
            />
            <input type="submit" value="Adicionar" />
          </form>

          <div className="botoes-acoes">
            <div className="add-excel">
              <button>+</button>
              <p>Adicionar múltiplos usuários via excel</p>
            </div>
            <div className="baixar-excel">
              <button>
                <img src={baixarexcel} alt="Ícone de download" />
              </button>
              <p>Excel para adicionar múltiplos usuários</p>
            </div>
          </div>
        </section>

        <section className="lista-usuarios">
          <h1>Usuários</h1>
          <div className="listagem-usuarios">
            {users.length === 0 && <p>Nenhum usuário adicionado ainda.</p>}

            {users.map((u) => (
              <div className="card-usuario" key={u.cd_usuario}>
                <div className="foto-informacoes-user">
                  <img src={randomProfile} alt="Imagem de perfil do usuário" />
                  <div className="informacoes-usuario">
                    <p className="nome-user">{u.nm_usuario}</p>
                    <p>{u.nm_email_usuario}</p>
                  </div>
                </div>

                <div className="acoes-card-usuario">
                  <button className="editar-user" onClick={() => abrirEdicao(u)}>
                    <img src={editIcon} alt="Ícone de edição" />
                  </button>
                  <button
                    className="deletar-user"
                    onClick={() => handleRemover(u.cd_usuario)}
                  >
                    <img src={deleteIcon} alt="Ícone de deleção" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          <button
            className="confirmar-users"
            onClick={handleConfirmar}
            disabled={enviando}
          >
            {enviando ? "Criando..." : "Confirmar criação"}
          </button>
        </section>

        {/* Modal de edição (cópia do user) */}
        {editando && (
          <div className="background-modal ativo">
            <div className="modal">
              <h2>Editar usuário</h2>
              <input
                type="text"
                name="nm_usuario"
                placeholder="Nome do usuário"
                value={editando.nm_usuario}
                onChange={onChangeEdicao}
              />
              <input
                type="email"
                name="nm_email_usuario"
                placeholder="Email do usuário"
                value={editando.nm_email_usuario}
                onChange={onChangeEdicao}
              />
              <input
                type="password"
                name="nm_senha_usuario"
                placeholder="Nova senha (opcional)"
                value={editando.nm_senha_usuario}
                onChange={onChangeEdicao}
              />
              <input
                type="password"
                name="nm_confirmar_senha"
                placeholder="Confirmar nova senha"
                value={editando.nm_confirmar_senha}
                onChange={onChangeEdicao}
              />
              <div className="botoes-modal">
                <button onClick={handleSalvarEdicao}>Salvar</button>
                <button onClick={() => setEditando(null)}>Cancelar</button>
              </div>
            </div>
          </div>
        )}

        {/* Modal de sucesso */}
        <div className={`background-modal ${ativarModalSucesso ? "ativo" : ""}`}>
          <div className="modal">
            <p>{infoModal}</p>
            <button onClick={() => setAtivarModalSucesso(false)}>Ok</button>
          </div>
        </div>

        {/* Modal de erro */}
        <div className={`background-modal ${ativarModalErro ? "ativo" : ""}`}>
          <div className="modal">
            <p>{infoModal}</p>
            <button onClick={() => setAtivarModalErro(false)}>Ok</button>
          </div>
        </div>
      </main>
    </>
  );
};

export default CreateUser;