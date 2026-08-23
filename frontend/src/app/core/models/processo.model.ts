export interface Processo {
  id: string;
  numeroUnico: string;
  areaDireito: string;
  status: string;
  responsavelId: string;
  responsavelNome?: string;
  cliente?: string;
  parteContraria?: string;
  comarca?: string;
  valorCausa?: number;
  dataDistribuicao?: string;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export interface ProcessoRequest {
  numeroUnico: string;
  areaDireito: string;
  responsavelId: string;
  cliente?: string;
  parteContraria?: string;
  comarca?: string;
  valorCausa?: number;
  dataDistribuicao?: string;
}
