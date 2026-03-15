package com.example.knowledgebase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
class KnowledgebaseApplicationTests {

  @Test
  void applicationClassCanBeInstantiated() {
    assertDoesNotThrow(KnowledgebaseApplication::new);
  }

}
