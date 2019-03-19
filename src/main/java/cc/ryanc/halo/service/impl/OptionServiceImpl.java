package cc.ryanc.halo.service.impl;

import cc.ryanc.halo.model.entity.Option;
import cc.ryanc.halo.model.enums.BlogProperties;
import cc.ryanc.halo.repository.OptionRepository;
import cc.ryanc.halo.service.OptionService;
import cc.ryanc.halo.service.base.AbstractCrudService;
import cc.ryanc.halo.utils.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;

/**
 * OptionService implementation class
 *
 * @author : RYAN0UP
 * @date : 2019-03-14
 */
@Service
public class OptionServiceImpl extends AbstractCrudService<Option, Integer> implements OptionService {

    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        super(optionRepository);
        this.optionRepository = optionRepository;
    }

    /**
     * Saves one option
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void save(String key, String value) {
        Assert.hasText(key, "Option key must not be blank");

        if (StringUtils.isNotBlank(value)) {
            // If the value is blank, remove the key
            optionRepository.removeByOptionKey(key);
            return;
        }

        Option option = optionRepository.findByOptionKey(key).map(anOption -> {
            // Exist
            anOption.setOptionValue(value);
            return anOption;
        }).orElseGet(() -> {
            // Not exist
            Option anOption = new Option();
            anOption.setOptionKey(key);
            anOption.setOptionValue(value);
            return anOption;
        });

        // Save or update the options
        optionRepository.save(option);
    }

    /**
     * Saves multiple options
     *
     * @param options options
     */
    @Override
    public void save(Map<String, String> options) {
        if (CollectionUtils.isEmpty(options)) {
            return;
        }

        // (Not recommended) Don't write "this::save" here
        // Types of key and value are String
        options.forEach((key, value) -> save(key, value));
    }

    @Override
    public void saveProperties(Map<BlogProperties, String> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }

        properties.forEach((property, value) -> save(property.getValue(), value));
    }

    /**
     * Gets all options
     *
     * @return Map
     */
    @Override
    public Map<String, String> listOptions() {
        return ServiceUtils.convertToMap(listAll(), Option::getOptionKey, Option::getOptionValue);
    }

    /**
     * Gets option by key
     *
     * @param key key
     * @return String
     */
    @Override
    public String getByKeyOfNullable(String key) {
        return getByKey(key).orElse(null);
    }

    @Override
    public Optional<String> getByKey(String key) {
        Assert.hasText(key, "Option key must not be blank");

        return optionRepository.findByOptionKey(key).map(Option::getOptionValue);
    }

    @Override
    public String getByPropertyOfNullable(BlogProperties property) {
        return getByProperty(property).orElse(null);
    }

    @Override
    public Optional<String> getByProperty(BlogProperties property) {
        Assert.notNull(property, "Blog property must not be null");

        return getByKey(property.getValue());
    }
}