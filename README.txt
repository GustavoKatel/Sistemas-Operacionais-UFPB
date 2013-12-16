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

Notação: TIPO:ID_REQUISIÇÃO:TEMPO_DE_CHEGADA:ARQUIVO_TAMANHO

Arquivos com o log de buffer e dos clientes encontram-se na pasta 'tests'

Test1 (FIFO):
--------------
Parâmetros servidor: 
5100 1 5 fifo

Parâmetros cliente:
localhost 5100 4 conc

Arquivos: 
test1_out.txt  -> Saída do cliente.
test1_buff.txt -> Representação do buffer.

Pode-se perceber que a primeira requisição atendida é sempre a primeira do buffer (índice 0), caracterizando FIRST-IN/FIRST-OUT.

Test2 (RANDOM):
---------------
Parâmetros servidor: 
5100 1 5 random

Parâmetros cliente:
localhost 5100 4 conc

Arquivos: 
test2_out.txt  -> Saída do cliente.
test2_buff.txt -> Representação do buffer.

Observe que a requisição de ID 4 apenas foi concluída no tempo 30734 levando em conta que ela chegou ao servidor em 6625. Enquanto que a requisição de ID 11 chegou aos 18112 e concluída em 29727. Vemos que ela, mesmo chegando depois, foi concluída em menos tempo, dando base ao algorítmo aleatório.

Test3 (SJF):
---------------
Parâmetros servidor: 
5100 1 5 sjf

Parâmetros cliente:
localhost 5100 4 conc

Arquivos: 
test3_out.txt  -> Saída do cliente.
test3_buff.txt -> Representação do buffer.

Perceba que a requisição de ID 2 foi a 14ª a ser concluída por ter um arquivo de tamanho maior. 
Observe também a requisição de ID 15, foi a última a entrar no buffer, no entanto foi a 12ª a ser atendida.

Test4 (PCGI):
---------------
Parâmetros servidor: 
5100 1 5 pcgi

Parâmetros cliente:
localhost 5100 4 conc

Arquivos: 
test4_out.txt  -> Saída do cliente.
test4_buff.txt -> Representação do buffer.

Observe que o primeiro elemento a ser atendido é um GET, apenas pelo fato de que o buffer estava sem requisições do tipo CGI. Isso ocorre também em outros pontos pelo mesmo motivo.
Observe também que ao final restaram apenas requisições do tipo GET.
Dando base ao algorítmo PCGI.

Test5 (PGET):
---------------
Parâmetros servidor: 
5100 1 5 pget

Parâmetros cliente:
localhost 5100 4 conc

Arquivos: 
test5_out.txt  -> Saída do cliente.
test5_buff.txt -> Representação do buffer.

É válido afirmar que o funcionamento é o mesmo do PCGI, o que pode ser observado na representação do buffer.
Este teste em particular gerou mais intercalações com requisições do tipo CGI, pelo simples fato da geração ser aleatória, ocasionando mais momentos de falta de requisições GET.
