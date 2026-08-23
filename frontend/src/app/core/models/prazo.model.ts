export interface Prazo {
  id: string;
  processoId: string;
  processoNumero?: string;
  responsavelId: string;
  responsavelNome?: string;
  descricao: string;
  dataVencimento: string;
  status: string;
  dataCumprimento?: string;
  dataCriacao?: string;
}

export interface PrazoRequest {
  processoId: string;
  responsavelId: string;
  dataVencimento: string;
  descricao: string;
}
