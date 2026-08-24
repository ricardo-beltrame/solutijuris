export interface UsuarioResumo {
  id: string;
  nomeCompleto: string;
  email: string;
  perfil: string;
}

export interface Processo {
  id: string;
  numeroUnico: string;
  areaDireito: string;
  tribunal: string;
  vara: string;
  status: string;
  assunto?: string;
  dataDistribuicao?: string;
  dataArquivamento?: string;
  segredoJustica: boolean;
  valorCausa?: number;
  responsavel: UsuarioResumo;
  poloAtivo?: string;
  poloPassivo?: string;
  criadoEm: string;
  atualizadoEm: string;
  ativo: boolean;
}

export interface ProcessoRequest {
  numeroUnico: string;
  areaDireito: string;
  tribunal: string;
  vara: string;
  assunto?: string;
  dataDistribuicao?: string;
  segredoJustica: boolean;
  valorCausa?: number;
  poloAtivo?: string;
  poloPassivo?: string;
  responsavelId: string;
}
