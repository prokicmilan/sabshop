import operations.ArticleOperations;
import student.pm160695_ArticleOperations;

public class StudentMain {

	public static void main(String[] args) {
		ArticleOperations articleOps = new pm160695_ArticleOperations();
		
		int id = articleOps.createArticle(1, "abcdef", 0);
		System.out.println(id);
	}
	
}
