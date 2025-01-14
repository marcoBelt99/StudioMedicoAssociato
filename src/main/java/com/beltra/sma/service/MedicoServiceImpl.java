package com.beltra.sma.service;

import com.beltra.sma.model.Medico;
import com.beltra.sma.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoServiceImpl(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<Medico> getAllMedici() {
        return medicoRepository.findAll();
    }
}
