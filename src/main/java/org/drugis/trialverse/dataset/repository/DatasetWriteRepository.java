package org.drugis.trialverse.dataset.repository;

import org.drugis.trialverse.security.Account;

/**
 * Created by connor on 04/11/14.
 */
public interface DatasetWriteRepository {
  String createDataset(String title, String description, Account owner);
}