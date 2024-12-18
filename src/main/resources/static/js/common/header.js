document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const searchResults = document.getElementById('searchResults');
    let searchTimeout;

    searchInput.addEventListener('input', function(e) {
        clearTimeout(searchTimeout);

        const searchTerm = e.target.value.trim();
        if (!searchTerm) {
            searchResults.style.display = 'none';
            return;
        }

        searchTimeout = setTimeout(() => {
            searchResults.style.display = 'block';
            searchResults.innerHTML = '<div class="loading">검색 중...</div>';

            const titleSearch = fetch(`/api/novels?title=${encodeURIComponent(searchTerm)}&page=1`)
                .then(response => response.ok ? response.json() : { novels: [] })
                .catch(() => ({ novels: [] }));

            const authorSearch = fetch(`/api/novels?author=${encodeURIComponent(searchTerm)}&page=1`)
                .then(response => response.ok ? response.json() : { novels: [] })
                .catch(() => ({ novels: [] }));

            Promise.all([titleSearch, authorSearch])
                .then(([titleData, authorData]) => {
                    const titleResults = titleData.novels || [];
                    const authorResults = authorData.novels || [];

                    // 결과가 없는 경우
                    if (titleResults.length === 0 && authorResults.length === 0) {
                        searchResults.innerHTML = '<div class="search-result-item">검색 결과가 없습니다.</div>';
                        return;
                    }

                    // 결과 표시
                    let resultHTML = '';

                    if (titleResults.length > 0) {
                        resultHTML += `
                            <div class="search-category">제목 검색 결과</div>
                            ${renderNovelResults(titleResults)}
                        `;
                    }

                    if (authorResults.length > 0) {
                        resultHTML += `
                            <div class="search-category">작가 검색 결과</div>
                            ${renderNovelResults(authorResults)}
                        `;
                    }

                    searchResults.innerHTML = resultHTML;
                });
        }, 300);
    });

    function renderNovelResults(novels) {
        return novels
            .map(novel => `
                <div class="search-result-item" onclick="location.href='/novels/${novel.novelId}'">
                    <div class="novel-title">${novel.title}</div>
                    <div class="novel-author">작가: ${novel.author}</div>
                </div>
            `)
            .join('');
    }

    // 검색창 외부 클릭시 결과창 숨기기
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !searchResults.contains(e.target)) {
            searchResults.style.display = 'none';
        }
    });
});