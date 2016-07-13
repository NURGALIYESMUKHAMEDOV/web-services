package kz.gamma.webra.test.core.generators;

import java.util.Map;

/**
 * Created by i_nikulin
 * 19.04.2010 16:44:17
 */

/**
 * Интерфейс для генераторов
 */
public abstract class Generator {

    //передаваемые параметры
    protected Map<String, String> params;

    protected boolean canError = true;

    public void init(Map<String, String> params) {
        this.params = params;
    }

    /**
     * метод генерирует правильное значение
     * @return правильное значение
     */
    public abstract String generate();

    /**
     * метод генерирует неправильное значение
     * @return неправильное значение
     */
    public abstract String generateError();


    public boolean isCanError() {
        return canError;
    }

    public void setCanError(boolean canError) {
        this.canError = canError;
    }
}
