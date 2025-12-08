package uk.gov.legislation.endpoints.ld.items;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.queries.ItemSort;

@Component
public class ItemSortConverter implements Converter<String, ItemSort> {

    @Override
    public ItemSort convert(String sort) {
        if (sort.isEmpty())
            return null;
        boolean desc = false;
        if (sort.startsWith("-")) {
            desc = true;
            sort = sort.substring(1);
        }
        return switch (sort) {
            case "number" -> desc ? ItemSort.NUMBER_DESC : ItemSort.NUMBER_ASC;
            case "citation" -> desc ? ItemSort.CITATION_DESC : ItemSort.CITATION_ASC;
            case "year" -> desc ? ItemSort.YEAR_DESC : ItemSort.YEAR_ASC;
            case "title" -> desc ? ItemSort.TITLE_DESC : ItemSort.TITLE_ASC;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown sort: " + sort);
        };
    }

}
