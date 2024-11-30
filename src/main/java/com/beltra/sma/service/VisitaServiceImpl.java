package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaDTO;
import com.beltra.sma.repository.VisitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;

    public VisitaServiceImpl(VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    @Override
    public List<VisitaDTO> getAllVisite() {
        return this.visitaRepository.findAllVisiteOrderByDataVisitaDesc();
    }


    @Override
    public List<VisitaDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username) {
        return this.visitaRepository.findAllVisitePrenotateAndNotEffettuateByUsernamePaziente(username);
    }
}
