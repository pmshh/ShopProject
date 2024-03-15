package com.windsome.service.product;

import com.windsome.entity.Size;
import com.windsome.repository.product.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeService {

    private final SizeRepository sizeRepository;

    public void addProductSize(String[] sizeNames) {
        for (String sizeName : sizeNames) {
            Size size = new Size();
            size.setName(sizeName);
            sizeRepository.save(size);
        }
    }

    public boolean existsByColorName(String[] sizeNames) {
        for (String sizeName : sizeNames) {
            if (sizeRepository.existsByName(sizeName)) {
                return true;
            }
        }
        return false;
    }
}
