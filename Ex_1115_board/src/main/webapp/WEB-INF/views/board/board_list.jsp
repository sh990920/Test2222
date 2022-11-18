<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

	<style>
		a{ text-decoration:none;
		   color:black;  }
		   
		table{border-collapse:collapse;}   
	</style>

</head>
<body>
	<table border="1" align="center" width="700">
		<caption>::: 게시판 :::</caption>
		
		<tr>
			<th width="50">번호</th>
			<th>제목</th>
			<th width="120">작성자</th>
			<th width="120">작성일</th>
			<th width="50">조회수</th>
		</tr>
		
		<c:forEach var="vo" items="${ list }">
			<tr>
				<td align="center">${vo.idx}</td>
				
				<td>
					<!-- 댓글 들여쓰기 -->
					<c:forEach begin="1" end="${ vo.depth }">&nbsp;</c:forEach>
					
					<!-- 댓글기호 -->
					<c:if test="${vo.depth ne 0 }">
					ㄴ
					</c:if>
					
					<c:if test="${ vo.del_info ne -1 }">
						<a href="view.do?idx=${vo.idx}&page=${param.page}">${vo.subject}</a>
					</c:if>
					
					<c:if test="${ vo.del_info eq -1 }">
						<font color="gray">${vo.subject}</font>
					</c:if>
				</td>
				
				
				<td align="center">${ vo.name }</td>
				
				<c:if test="${ vo.del_info ne -1 }">
					<td align="center">${fn:split(vo.regdate,' ')[0]}</td>
				</c:if>
				
				<c:if test="${ vo.del_info eq -1 }">
					<td align="center">unknown</td>
				</c:if>
				
				<td align="center">${ vo.readhit }</td>
			</tr>
		</c:forEach>
		
		<tr>
			<td colspan="5" align="center">
				${ pageMenu }
			</td>
		</tr>
		
		<tr>
			<td colspan="5" align="right">
				<input type="button" value="새글쓰기"
				       onclick="location.href='insert_form.do'"/>
			</td>
		</tr>
		
	</table>
</body>
</html>




































































