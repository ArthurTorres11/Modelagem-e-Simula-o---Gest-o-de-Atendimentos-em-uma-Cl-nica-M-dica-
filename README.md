Simulação Clínica – Gestão de Atendimentos (Clínica Vida Saudável)
Este projeto modela, simula e analisa o atendimento em clínicas médicas usando eventos discretos para tomada de decisão na gestão de consultórios, priorização, fila única e inovação tecnológica.

Funcionalidades
Simulação do fluxo completo de pacientes (chegada, triagem, fila, atendimento)

Análise de múltiplos cenários operacionais:

Quantidade variada de consultórios

FIFO x priorização de urgentes

Fila única ou múltiplas filas por médico

Impacto do novo sistema eletrônico de triagem

Ajuste dinâmico por nível de demanda

Geração de tabelas e relatórios automáticos (.tsv, .html)

Scripts para estatísticas e visualização de resultados


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


