1.数据库，里面有俩文件，一个是QingNang的数据库，一个是日志文件。下载sql server 2005 express，然后下载一个SQL Server Management Studio Express，GUI的数据库管理器。然后把“数据库”文件夹中的两个文件导入数据库中。然后创建你自己的用户名、密码，把这个QingNang数据库的owner权限赋予你创建的用户名密码。
2.document_project文件集里面是一个chm的说明文件，是json协议对应的类型的c#定义的说明。你可以照着，做一个java下的定义。
3.PCServer里，是chm文件中对应类型的源码。以及PC上的模拟Client端的源码工程。
4.QingNang里面是整个工程的可执行文件，分Debug和Release。目前只有前者。Loader是Server端的主程序。TestClient是PC上的模拟Client端。其下的NewSoftWare下放的是模拟的更新文件。Config里面放的是配置文件，在其下的software_config.xml文件中，<DataBase ConnectString="Data Source = lpc:(local)\OiMAXSQL; Database = QingNang; User ID = QingNang; Password = asdfg12345"></DataBase>的Data Source = lpc:(local)\×××，请将×××换成你的sql的服务名称，User ID换成1步骤中创建的用户名，Password换成对应的密码。
5.Android参考代码下，有对应的java参考代码和TestClient的C#代码，以及一个word文档的协议说明。