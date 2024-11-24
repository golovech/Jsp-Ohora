package mvc.command;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.util.ConnectionProvider;

import ohora.domain.PagingVO;
import ohora.domain.ProductDTO;
import ohora.persistence.OhoraDAO;
import ohora.persistence.OhoraDAOImpl;

public class PrdListHandler implements CommandHandler{
	
	int currentPage = 1;				// 현재 페이지 번호
	int numberPerPage = 12;				// 한 페이지에 출력할 게시글 수
	int numberOfPageBlock = 10;			// [1] 2 3 4 5 6 7 8 9 10 >
	int totalRecords = 0;				// 총 레코드 수
	int totalPages = 0;					// 총 페이지 수
	int categoryNumber = 0;
	int sort_method = 0;				// 상품 정렬
	
	int first=1;
	int prev;
	int next;
	int last;

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("PrdList Handler..");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
		
        HttpSession session = httpRequest.getSession();
		
		try {
			this.currentPage = Integer.parseInt(request.getParameter("currentPage"));
			
		} catch (Exception e) {
			this.currentPage = 1;
		}
		
		try {
			this.categoryNumber = Integer.parseInt(request.getParameter("cate_no"));		
		} catch (Exception e) {
			this.categoryNumber = 0;
		}
		
		try {
			this.sort_method = Integer.parseInt(request.getParameter("sort_method"));		
		} catch (Exception e) {
			this.sort_method = 0;
		}
		
		Connection conn = ConnectionProvider.getConnection();
		OhoraDAO dao = new OhoraDAOImpl(conn);
		ArrayList<ProductDTO> list = null;
		
		PagingVO pvo = null;
		
		if( session.getAttribute("userPk") == null && categoryNumber == 671 ) {
			String loginUrl = httpRequest.getContextPath() + "/ohora/login.jsp?loginCheck=fail";
			httpResponse.sendRedirect(loginUrl);
			return null;
		}
		
		try {
				pvo = new PagingVO(currentPage, numberPerPage, numberOfPageBlock, categoryNumber);
				
				// 상품 정렬 없을 때
				if ( categoryNumber != 0 && sort_method == 0 ) {
					list = dao.prdCate(this.currentPage, this.numberPerPage, this.categoryNumber);
				// 상품 정렬 있을 때
				}else {
					list = dao.prdCate(this.currentPage, this.numberPerPage, this.categoryNumber, this.sort_method);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
				
				request.setAttribute("list", list);
				request.setAttribute("pvo", pvo);
				
				String path = "/ohora/prd-nail-page.jsp";
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				
				return "/ohora/prd-nail-page.jsp";
				
		}
}