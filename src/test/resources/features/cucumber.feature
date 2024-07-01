Feature: Post functionality

  Scenario Outline: As a customer I want to create a new post
    Given I can create a new post
    When I send a post to be created with title "<title>" and content "<content>"
    Then Response status should be 201
    And I should be able to see my newly created post

    Examples:
      | title          | content           |
      | Post 10        | Post content      |
      | Titlu  postare | Continut  postare |


  Scenario Outline: As a customer I want to update an existing post
    Given I can update the post with ID "<id>"
    When I update the post with ID "<id>" to have the title "<updatedTitle>" and the content "<updatedContent>"
    Then The post with ID "<id>" should have the title "<updatedTitle>" and the content "<updatedContent>"

    Examples:
      | id | updatedTitle | updatedContent  |
      | 2  | Updated Post | Updated Content |

  Scenario: Get all posts
    Given I can request to see all posts
    When I request all posts
    Then The response status should be 200
