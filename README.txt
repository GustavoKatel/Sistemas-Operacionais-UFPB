Gustavo Brito - 11118146
Rafael Germano - 11121188

Desenvolvimento presencial, a dupla encontrou-se várias vezes durante o período para desenvolver o projeto. Discutindo e testando de forma que os dois entendiam e colaboravam de maneira equivalente.

Sobre
=======

	- Classes desenvolvidas no projeto:
	+ Descritor: classe modelo para descritor. Descritores contém os atributos:
		+ String de requisição
		+ tipo de requisição
		+ nome do arquivo ( executável, caso tipo seja CGI )
		+ tempo de chegada: tempo do início do servidor até o momento de chegada da requisição/criação do descritor
		+ total de requisições agendadas: Número de requisições/descritores no buffer no momento da chegada
		+ tempo de agendamento: tempo entre a requisição e a entrada no buffer.
		+ idade: quantidade de vezes que o descritor foi ultrapassado por outros devido ao escalonamento.
		+ tamanho do arquivo: tamanho do arquivo em bytes que vai ser processado.

	+ WorkerThread: Classe responsável pelo processamento do buffer. Atributos:
		+ id_thread: número de identificação
		+ contador_thread: número de requisições que ela processou.

	+ Buffer

		O buffer foi dividido em 6 classes, sendo que a classe Buffer funciona como super classe das demais, generalizando trechos comuns a todas, como a adição de descritores. As demais diferem-se no tipo de escalonamento ( método 'getNext' ). 

		Não foram utilizados métodos de ordenação e sim métodos de recuperação de dados. Assim, a adição de um novo 'Descritor' no buffer é sempre ao final, seguindo a ideia de fila, no entando a leitura/remoção é feita de maneira diferente dependendo do escalonamento.

		Atributos:

		+ total: número de requisições/descritores no buffer.
		+ totalRequisiçõesAtendidas: total de requisições atendidas até o momento.
		+ totalConcluidas: total de requisições que foram atendidas e concluídas até o momento.

Tipos de escalonamento:
-------------------------

		+ FIFO ( BufferFifo )
		+ PCGI ( BufferPCGI )
		+ PGET ( BufferPGET )
		+ Random ( BufferRandom )
		+ SJF ( BufferSJF )
	

BUGS
------------------------	

	Foi encontrado um bug referente ao cliente com escalonemento do tipo FIFO e requisições do tipo CGI, onde apenas uma thread é alocada para as requisições quando se ultrapassa o limite threads em requisições.

Testes
------------------------

Para fins de teste, retardamos o tempo de leitura do buffer.

Obs.: Com apenas uma thread no servidor, será atendida apenas uma requisição por vez.

Test1 (FIFO):
--------------
Parâmetros servidor: 
5100 1 5 fifo

Parâmetros cliente:
localhost 5100 4 conc

Notação: TIPO:TEMPO_DE_CHEGADA

Arquivos: 
test1_out.txt  -> Saída do cliente.
test1_buff.txt -> Representação do buffer.


Test2 (RANDOM):
---------------
Parâmetros servidor: 
5100 1 5 random

Parâmetros cliente:
localhost 5100 4 conc

Notação: TIPO:ID_REQUISIÇÃO:TEMPO_DE_CHEGADA

Arquivos: 
test2_out.txt  -> Saída do cliente.
test2_buff.txt -> Representação do buffer.

Observe que a requisição de ID 4 apenas foi concluída no tempo 40078 levando em conta que ela chegou ao servidor em 7745. Enquanto que a requisição de ID 12 chegou aos 22397 e concluída em 33746. Vemos que ela mesmo chegando depois foi concluída em menos tempo, dando base ao algorítmo aleatório.
