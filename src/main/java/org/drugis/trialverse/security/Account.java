/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drugis.trialverse.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class Account implements UserDetails {

  private Integer id;

  private String username;
  private String firstName;
  private String lastName;
  private String userNameHash;
  private GrantedAuthority userRole = new SimpleGrantedAuthority("ROLE_USER");

  protected Account() {
  }

  public Account(String username, String firstName, String lastName, String userNameHash) {
    this(-1, username, firstName, lastName, userNameHash);
  }

  public Account(int id, String username, String firstName, String lastName, String userNameHash) {
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userNameHash = userNameHash;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(userRole);
  }

  @Override
  public String getPassword() {
    return null;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getuserNameHash() {
    return userNameHash;
  }

  public void setuserNameHash(String userNameHash) {
    this.userNameHash = userNameHash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (!username.equals(account.username)) return false;
    if (firstName != null ? !firstName.equals(account.firstName) : account.firstName != null) return false;
    if (lastName != null ? !lastName.equals(account.lastName) : account.lastName != null) return false;
    return userNameHash.equals(account.userNameHash);

  }

  @Override
  public int hashCode() {
    int result = firstName != null ? firstName.hashCode() : 0;
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + userNameHash.hashCode();
    return result;
  }
}
