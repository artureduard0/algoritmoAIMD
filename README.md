# algoritmoAIMD
Programa feito em Java com a implementação do algoritmo AIMD original em nível de aplicação no UDP e TCP.

Informações gerais:
- Por padrão o limite de envio é 2^5 onde 5 é o N é pode ser alterado no atributo "limiteDeN" do cliente. 
- O timeout funciona também para simulação de perdas e pode ser alterado no atributo "timeout". 
- As portas devem ser as mesmas no cliente e no servidor para que possam trocar dados.
- A janela é dividida pela metade quando há perda de dados.
