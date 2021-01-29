package com.ris.inventory.pos.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.DownloadDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.model.response.Response;
import com.ris.inventory.pos.util.DocumentUtil;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.enumeration.Period;
import com.ris.inventory.pos.util.exception.BadRequestException;
import com.ris.inventory.pos.util.exception.NotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public interface ReportService<R extends Response> {

    List<String> KEYS = Arrays.asList("start", "end", "transactionType", "period", "locationId");

    PaginationDTO<R> report(int offset, int limit, CurrentUser currentUser);

    List<R> report(CurrentUser currentUser);

    PaginationDTO<R> filter(List<FilterType> filterType, FilterCO value, int offset, int limit, CurrentUser currentUser);

    List<R> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser);

    DownloadDTO download(List<String> keys) throws JsonProcessingException;

    default Date getPeriod(Period period) {
        LocalDate today = LocalDate.now();

        switch (period) {
            case TODAY:
                return Date.from(today.atStartOfDay().toInstant(ZoneOffset.UTC));
            case YESTERDAY:
                return Date.from(today.minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
            case WEEKLY:
                return Date.from(today.minusDays(7).atStartOfDay(ZoneOffset.UTC).toInstant());
            case MONTHLY:
                return Date.from(today.minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant());
            default:
                throw new NotFoundException("Period not found exception. Please try with correct input");
        }
    }

    default List<Date> getDatesTill(LocalDate lastDate) {
        List<Date> dates = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        LocalDate calculatedDate = lastDate;
        do {
            if (currentDate.equals(lastDate)) {
                dates.add(Date.from(lastDate.atStartOfDay(ZoneOffset.UTC).toInstant()));
            } else {
                calculatedDate = calculatedDate.plusDays(1);
                dates.add(Date.from(calculatedDate.atStartOfDay(ZoneOffset.UTC).toInstant()));
            }
        } while (!calculatedDate.equals(currentDate));
        return dates;
    }

    default float calculateRevenueOf(List<Transaction> transactions) {
        float revenue = 0.0f;

        for (Transaction transaction : transactions)
            revenue += transaction.getPayableAmount();

        return revenue;
    }

    default FilterCO fetchJSON(String json) {
        Logger log = LoggerFactory.getLogger(ReportService.class);

        FilterCO filterCO = null;
        try {
            JSONObject object = new JSONObject(json);
            log.info("Filter JSON params {}", object);
            List<Boolean> status = new ArrayList<>();
            Set<String> keys = object.keySet();
            keys.forEach(
                    it -> {
                        if (KEYS.contains(it))
                            status.add(true);
                    }
            );
            if (status.size() <= 4)
                throw new BadRequestException("Bad filter JSON params.");

            filterCO = new Gson().fromJson(object.toString(), FilterCO.class);
        } catch (Exception exception) {
            throw new BadRequestException("Bad filter JSON params.");
        }
        return filterCO;
    }

    default <T> List<String> getColumns(List<T> objects) throws JsonProcessingException {
        List<String> columns = new ArrayList<>();
        if (!objects.isEmpty()) {
            columns = DocumentUtil.convertColumnNameCase(new JSONObject(new ObjectMapper().writeValueAsString(objects.get(0))));
        }

        return columns;
    }

    default <T> List<String> getActualColumns(List<T> objects) throws JsonProcessingException {
        List<String> columns = new ArrayList<>();
        if (!objects.isEmpty())
            columns.addAll(new JSONObject(new ObjectMapper().writeValueAsString(objects.get(0))).keySet());
        return columns;
    }

    default <T> JSONArray toJSON(List<T> objects) throws JsonProcessingException {
        return new JSONArray(new ObjectMapper().writeValueAsString(objects));
    }
}
