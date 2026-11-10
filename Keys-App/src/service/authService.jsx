import auth from "./auth";

export async function login(email, senha) {

    try {

        const response = await auth.post("/login", {
            email: email,
            senha: senha
        });

        const token = response.data.token;

        localStorage.setItem("token", token);

        return response.data;

    } catch (error) {

        console.error("Erro ao realizar login:", error);

        throw error;
    }
}