package run.halo.app.model.dto.base;

import run.halo.app.utils.ReflectionUtils;
import run.halo.app.utils.BeanUtils;
import run.halo.app.utils.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import static run.halo.app.utils.BeanUtils.transformFrom;
import static run.halo.app.utils.BeanUtils.updateProperties;

/**
 * Converter interface for input DTO.
 *
 * @author johnniang
 */
public interface InputConverter<DOMAIN> {

    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @SuppressWarnings("unchecked")
    default DOMAIN convertTo() {
        // Get parameterized type
        ParameterizedType currentType = ReflectionUtils.getParameterizedType(InputConverter.class, this.getClass());

        // Assert not equal
        Objects.requireNonNull(currentType, "Cannot fetch actual type because parameterized type is null");

        Class<DOMAIN> domainClass = (Class<DOMAIN>) currentType.getActualTypeArguments()[0];

        return BeanUtils.transformFrom(this, domainClass);
    }

    /**
     * Update a domain by dto.(shallow)
     *
     * @param domain updated domain
     */
    default void update(DOMAIN domain) {
        BeanUtils.updateProperties(this, domain);
    }
}
