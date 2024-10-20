# 🌟 Solutech - App de Gestão de Investimentos

Este projeto é um aplicativo Android desenvolvido com **Jetpack Compose** e integrado ao **Firebase** para persistência de dados. O aplicativo oferece uma gama de funcionalidades focadas na gestão de investimentos, conversão de moedas e acesso a notícias financeiras.

## Integrantes:

- [X] 550202 Gabriel Melo dos Santos
- [X] 550181 Gustavo Ferreira de Araujo
- [X] 95003 Igor Ferreira Santana
- [X] 97757 Lucas Fernando Andrade Spinelli
- [X] 552262 YURI CHICHEDOM IKEGWUONU


## Funcionalidades

### 1. Notícias Financeiras
- Exibição de notícias atualizadas sobre o mercado financeiro.
- As notícias incluem imagem, título, descrição e data de publicação.
- Interface simples e intuitiva para manter o usuário informado.

### 2. Conversor de Moedas
- Ferramenta de conversão entre diferentes moedas, com base nas taxas de câmbio atualizadas.
- Suporte a várias moedas, com opção de selecionar moeda de origem e destino.
- Taxas de câmbio obtidas automaticamente pela API ExchangeRate-API.

### 3. Cálculo de Investimentos
- Simulação de investimentos com base em diferentes cenários.
- O usuário pode selecionar o tipo de investimento (Tesouro Direto, LCI/LCA, CDB/LC).
- Definição do tipo de taxa (Pré-fixada, Pós-fixada ou IPCA).
- Insere-se o valor inicial, investimento mensal, prazo e a rentabilidade anual para gerar a simulação.

### 4. Autenticação de Usuário
- Autenticação de usuários com **Firebase Authentication**.
- Login e registro de usuários.
- Verificação por e-mail com código de confirmação.

### 5. Persistência de Dados
- Uso do **Firebase Realtime Database** para armazenar dados do usuário, como perfil e histórico de simulações.
- Dados são sincronizados em tempo real com o backend.

## Tecnologias Utilizadas

- **Jetpack Compose**: Framework moderno para desenvolvimento de interfaces nativas no Android.
- **Firebase**: Para autenticação, armazenamento e sincronização de dados.
- **ExchangeRate-API**: API de conversão de moedas para obter taxas de câmbio atualizadas.
- **Kotlin**: Linguagem de programação usada para o desenvolvimento do app.
- **NodeMailer**: Ferramenta usada para envio de e-mails de confirmação de cadastro.

## Pré-requisitos para rodar o projeto

- Android Studio instalado.
- Conta no Firebase para configuração dos serviços de autenticação e banco de dados.
- Chave de API para o serviço de câmbio (ExchangeRate-API).


