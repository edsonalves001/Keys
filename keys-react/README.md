# Keys (React + Vite)

Versão em React do projeto Keys. As telas, os estilos e o comportamento são os mesmos da versão em HTML.

## Como rodar
```bash
npm install
cp .env.example .env     # ajuste VITE_API_URL para o endereço da sua API
npm run dev              # http://localhost:5173
npm run build            # gera a pasta dist/
```

## Rotas (antes eram arquivos .html)
| Antes               | Agora                |
|---------------------|----------------------|
| index.html          | `/`                  |
| login.html          | `/login`             |
| recuperar.html      | `/recuperar`         |
| nova-senha.html     | `/nova-senha`        |
| cronograma.html     | `/cronograma`        |
| reserva.html        | `/reserva`           |
| usuarios.html       | `/usuarios`          |
| adicionar-usuario.html | `/adicionar-usuario` |
| editar-reserva.html | `/editar-reserva`    |
| registro.html       | `/registro`          |

## Estrutura
```
src/
  main.jsx, App.jsx          entrada e rotas
  styles/base.css            cores (claro/escuro), cabeçalho, botões, painéis
  context/                   ThemeContext (modo claro/escuro) e ToastContext (avisos)
  components/                Header, Avatar, Timeline, Abas, PlanilhaPanel, PasswordField
  utils/helpers.js           funções de data, hora e cores
  service/api.js             instância do axios (VITE_API_URL + token)
  pages/<Pagina>/            <Pagina>.jsx + <pagina>.css (um CSS por página)
  pages/Cronograma/service/cronogramaApi.js   suas funções da API
```

## Detalhes importantes
- **CSS por página com escopo:** como no React todos os CSS importados ficam ativos ao mesmo tempo, cada arquivo de página foi
  prefixado com uma classe (`.p-login`, `.p-usuarios`...) para que classes de mesmo nome (`.cols2`, `.sf`, `.ph`) não se misturem.
  Ao criar uma página nova, siga o mesmo padrão: `<main className="pg p-minha-pagina">` e prefixe as regras do CSS dela.
- **Cronograma e API:** `cronogramaApi.js` usa o import `../../../service/api` (o axios de `src/service/api.js`).
  Os nomes de campos e o enum de status estão no começo de `Cronograma.jsx` (`STATUS`, `normSala`, `normReserva`).
  Se a API não responder, a tela mostra os dados de exemplo.
- **Dados de exemplo:** usuários, salas ocupadas e horários livres das demais telas são fixos no código, como na versão HTML.
- **Hospedagem:** como o projeto usa `BrowserRouter`, o servidor precisa devolver o `index.html` para qualquer rota
  (no Vite dev e `vite preview` já funciona).
