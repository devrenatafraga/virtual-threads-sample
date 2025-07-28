Olá! Preciso da sua ajuda para gerenciar meu repositório Git. Por favor, siga os passos abaixo:

Observação geral: Sempre escrever commits e pull requests inglês americano.

1.  **Criação da Branch:**
    *   A partir da branch `main`, crie uma nova branch chamada `[tipo]/[nome-descritivo-da-tarefa]`.
    *   Exemplos de `[tipo]`: `feature`, `fix`, `refactor`, `docs`.
    *   Exemplo de nome: `feature/adicionar-autenticacao-usuario`.
    * Verifique os tipos de alteração do projeto para definir qual o nome da branch mais adequado.


2.  **Execução dos Commits:**
    *   Realize os commits abaixo, separando as alterações por contexto. Para cada commit, adicione os arquivos relevantes (`git add`) e depois crie o commit (`git commit -m "mensagem"`).

    *   **Commit 1: [Título do primeiro commit, ex: feat: Adiciona endpoint de login]**
        *   Arquivos a serem alterados/criados:
            *   `[caminho/completo/para/o/arquivo1.java]`
            *   `[caminho/completo/para/o/arquivo2.java]`
        *   Descrição das alterações: [Descreva o que foi feito nestes arquivos para este commit específico].
        * Use commits semânticos para facilitar a compreensão do histórico do projeto.
        * Use gitmojis para facilitar na dinamicidade do commit.
    *   **(Exemplo: `git commit -m ":sparkles: Adiciona endpoint de login"`)**

    *   **Commit 2: [Título do segundo commit, ex: refactor: Melhora performance da consulta ao banco]**
        *   Arquivos a serem alterados/criados:
            *   `[caminho/completo/para/o/arquivo3.java]`
        *   Descrição das alterações: [Descreva a refatoração ou a segunda parte da sua tarefa].

    *   **(Continue adicionando blocos de commit conforme necessário)**

3.  **Push para o Repositório Remoto:**
    *   Após realizar todos os commits, faça o push da nova branch para o repositório remoto (`origin`).

4.  **Abertura do Pull Request (PR):**
    *   Abra um Pull Request da branch recém-criada para a branch `main`.
    *   **Título do PR:** `[Título conciso e informativo, ex: feat: Implementa sistema de autenticação]`
    * Use gitmoji para descrição da PR
    
    *   **Descrição do PR:**
        """
        **O que este PR faz?**
        [Descreva em alto nível o objetivo principal do Pull Request. O que ele resolve ou adiciona?]

        **Principais mudanças:**
        - [Mudança 1: Adicionado o serviço de autenticação]
        - [Mudança 2: Criado o endpoint /api/login]
        - [Mudança 3: Atualizada a documentação da API]

        **Como testar?**
        [Forneça um passo a passo simples para que outra pessoa possa validar suas alterações.]
        1. Suba a aplicação.
        2. Faça uma requisição POST para `/api/login` com o corpo `{"user": "teste", "pass": "123"}`.
        3. Verifique se o retorno é um token JWT válido.

        **Observações:**
        [Qualquer informação adicional, como dependências, configurações de ambiente ou pontos que merecem atenção especial.]
        """

Por favor, execute os comandos no terminal para completar todas as etapas.