package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    public static CategoryRepository categoryRepository;

    @Autowired
    private CategoryService(@Qualifier("categoryRepository") CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
