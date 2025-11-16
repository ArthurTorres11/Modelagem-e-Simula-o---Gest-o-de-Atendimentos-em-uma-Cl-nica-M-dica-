Simulação de Gestão de Atendimentos – Clínica Vida Saudável
Este projeto é um modelo de simulação de eventos discretos para dimensionamento e análise operacional de clínicas médicas, com foco em gestão de consultórios, dinâmica de filas, priorização de atendimentos e avaliação de alternativas tecnológicas.

Funcionalidades
Modelagem do fluxo de atendimento de pacientes (chegada, triagem, fila, atendimento)

Experimentação de diferentes cenários:

Números variados de consultórios

Algoritmo FIFO vs. priorização de urgentes

Fila única vs. múltiplas filas

Novo sistema de triagem eletrônico

Variação de demanda ao longo do dia

Cálculo automático de métricas: tempo médio de espera, fila máxima, throughput, etc.

Relatórios automáticos em HTML/TSV para análise posterior

Estrutura do Projeto
text
src/
└── com/
    └── clinica/
        ├── entity/
        │   ├── Paciente.java
        │   └── TipoPaciente.java
        ├── process/
        │   ├── GeradorPacientes.java
        │   └── ProcessoAtendimento.java
        ├── model/
        │   ├── ClinicaModel.java
        │   └── Config.java
        ├── util/
        │   └── ReportManager.java
        └── experiment/
            ├── ClinicaExperiment.java
            ├── RelatorioTempoEspera.java
            └── RelatorioComparativoNovoSistema.java
Cada pacote tem uma responsabilidade clara: entidades, regras de processo, configuração, utilidades de relatório e scripts de experimentação/análise.


Licença
MIT

Autor
Arthur Torres - RA: 740410
Flavio Farias - RA: 741799
 — [link do seu perfil]
