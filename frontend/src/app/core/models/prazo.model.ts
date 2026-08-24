export interface Prazo {
  id: string;
  processo: {
    id: string;
    numeroUnico: string;
  };
  responsavel: {
    id: string;
    nomeCompleto: string;
  };
  descricao: string;
  dataVencimento: string;
  dataCumprimento?: string;
  status: string;
  notificado15Dias: boolean;
  notificado7Dias: boolean;
  notificado3Dias: boolean;
  notificado1Dia: boolean;
  notificadoVencido: boolean;
  criadoEm: string;
  atualizadoEm: string;
  ativo: boolean;
}

export interface PrazoRequest {
  processoId: string;
  responsavelId: string;
  dataVencimento: string;
  descricao: string;
}
