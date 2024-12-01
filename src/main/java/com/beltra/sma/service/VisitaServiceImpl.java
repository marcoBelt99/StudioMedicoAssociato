package com.beltra.sma.service;

import com.beltra.sma.dto.VisitaPrenotataDTO;
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
    public List<VisitaPrenotataDTO> getAllVisite() {
        return this.visitaRepository.findAllVisiteOrderByDataVisitaDesc();
    }

    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndNotEffettuateByUsernamePaziente(String username) {
        return this.visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, false);
    }

    @Override
    public List<VisitaPrenotataDTO> getAllVisitePrenotateAndEffettuateByUsernamePaziente(String username) {
        return this.visitaRepository.findAllVisitePrenotateByUsernamePaziente(username, true);
    }
}
