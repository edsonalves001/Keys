import { corAvatar, iniciais } from "../utils/helpers";

export default function Avatar({ nome, big = false, cor }) {
  return (
    <span className={`ini${big ? " big" : ""}`} style={{ background: cor || corAvatar(nome) }} aria-hidden="true">
      {iniciais(nome)}
    </span>
  );
}
