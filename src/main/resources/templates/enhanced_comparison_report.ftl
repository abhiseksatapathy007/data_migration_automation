<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${reportTitle}</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        .summary {
            padding: 30px;
            border-bottom: 1px solid #eee;
        }
        .summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .summary-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            border-left: 4px solid #007bff;
        }
        .summary-card h3 {
            margin: 0 0 10px 0;
            color: #333;
            font-size: 1.2em;
        }
        .summary-card .number {
            font-size: 2em;
            font-weight: bold;
            color: #007bff;
        }
        .summary-card.passed {
            border-left-color: #28a745;
        }
        .summary-card.passed .number {
            color: #28a745;
        }
        .summary-card.failed {
            border-left-color: #dc3545;
        }
        .summary-card.failed .number {
            color: #dc3545;
        }
        .summary-card.error {
            border-left-color: #ffc107;
        }
        .summary-card.error .number {
            color: #ffc107;
        }
        .database-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-top: 20px;
        }
        .db-card {
            background: #e9ecef;
            padding: 15px;
            border-radius: 6px;
        }
        .db-card h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
        .db-card p {
            margin: 0;
            font-family: monospace;
            color: #6c757d;
        }
        .results-section {
            padding: 30px;
        }
        .results-section h2 {
            color: #333;
            margin-bottom: 20px;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .result-item {
            background: #f8f9fa;
            margin-bottom: 20px;
            border-radius: 8px;
            overflow: hidden;
            border: 1px solid #dee2e6;
        }
        .result-header {
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            background: #e9ecef;
            border-bottom: 1px solid #dee2e6;
        }
        .result-header:hover {
            background: #dee2e6;
        }
        .result-title {
            font-weight: 600;
            color: #495057;
        }
        .result-status {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: 600;
            text-transform: uppercase;
        }
        .status-pass {
            background: #d4edda;
            color: #155724;
        }
        .status-fail {
            background: #f8d7da;
            color: #721c24;
        }
        .status-error {
            background: #fff3cd;
            color: #856404;
        }
        .result-content {
            padding: 20px;
            display: none;
        }
        .result-content.expanded {
            display: block;
        }
        .row-count-comparison {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        .count-card {
            background: white;
            padding: 15px;
            border-radius: 6px;
            border: 1px solid #dee2e6;
        }
        .count-card h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
        .count-number {
            font-size: 1.5em;
            font-weight: bold;
            color: #007bff;
        }
        .details {
            background: white;
            padding: 15px;
            border-radius: 6px;
            border: 1px solid #dee2e6;
            margin-top: 15px;
        }
        .details h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
        .details p {
            margin: 0;
            color: #6c757d;
        }
        .query-section {
            background: white;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
            border: 1px solid #dee2e6;
        }
        
        /* Mismatch Data Table Styles */
        .mismatch-table-section {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 6px;
            padding: 20px;
            margin: 20px 0;
        }
        
        .mismatch-table-section h4 {
            margin: 0 0 15px 0;
            color: #856404;
            display: flex;
            align-items: center;
        }
        
        .mismatch-table-section h4::before {
            content: "⚠️";
            margin-right: 8px;
        }
        
        .mismatch-data-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 6px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .mismatch-data-table th {
            background: #f8f9fa;
            padding: 12px 8px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid #dee2e6;
        }
        
        .mismatch-data-table td {
            padding: 10px 8px;
            border-bottom: 1px solid #dee2e6;
            vertical-align: top;
        }
        
        .mismatch-data-table tr:hover {
            background-color: #f8f9fa;
        }
        
        .row-number {
            font-weight: 600;
            color: #495057;
            text-align: center;
        }
        
        .column-name {
            font-weight: 500;
            color: #6c757d;
        }
        
        .source-data, .target-data {
            font-family: 'Courier New', monospace;
            background: #f8f9fa;
            padding: 4px 8px;
            border-radius: 4px;
            border: 1px solid #dee2e6;
            word-break: break-all;
            max-width: 200px;
        }
        
        .source-data {
            border-left: 3px solid #28a745;
        }
        
        .target-data {
            border-left: 3px solid #dc3545;
        }
        
        .difference {
            text-align: center;
            font-weight: 600;
        }
        
        .diff-indicator {
            color: #dc3545;
            font-size: 18px;
        }
        
        /* Action Buttons */
        .actions {
            text-align: center;
            white-space: nowrap;
        }
        
        .detail-link, .investigate-link {
            display: inline-block;
            padding: 6px 12px;
            margin: 2px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 0.85em;
            font-weight: 500;
            transition: all 0.2s ease;
        }
        
        .detail-link {
            background: #007bff;
            color: white;
        }
        
        .detail-link:hover {
            background: #0056b3;
            transform: translateY(-1px);
        }
        
        .investigate-link {
            background: #6f42c1;
            color: white;
        }
        
        .investigate-link:hover {
            background: #5a32a3;
            transform: translateY(-1px);
        }
        
        /* SQL Query Display Styles */
        .sql-query-section {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 15px;
            margin: 15px 0;
        }
        
        .sql-query-section h4 {
            margin: 0 0 10px 0;
            color: #495057;
            font-size: 14px;
            display: flex;
            align-items: center;
        }
        
        .sql-query-section h4::before {
            content: "🔍";
            margin-right: 8px;
        }
        
        .sql-query-container {
            position: relative;
        }
        
        .sql-query {
            background: #2d3748;
            color: #e2e8f0;
            padding: 15px;
            border-radius: 6px;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            line-height: 1.4;
            overflow-x: auto;
            margin: 0;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 200px;
            overflow-y: auto;
        }
        
        .copy-sql-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background: #28a745;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 11px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .copy-sql-btn:hover {
            background: #218838;
            transform: scale(1.05);
        }
        
        /* Table Container */
        .table-container {
            overflow-x: auto;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .toggle-btn {
            background: none;
            border: none;
            color: #007bff;
            cursor: pointer;
            font-size: 0.9em;
            text-decoration: underline;
        }
        .toggle-btn:hover {
            color: #0056b3;
        }
        .customer-badge {
            background: #17a2b8;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>${reportTitle}</h1>
            <p>Generated on ${generatedAt}</p>
        </div>
        
        <div class="summary">
            <div class="summary-grid">
                <div class="summary-card">
                    <h3>Total Tests</h3>
                    <div class="number">${totalTests}</div>
                </div>
                <div class="summary-card passed">
                    <h3>Passed</h3>
                    <div class="number">${passedTests}</div>
                </div>
                <div class="summary-card failed">
                    <h3>Failed</h3>
                    <div class="number">${failedTests}</div>
                </div>
                <div class="summary-card error">
                    <h3>Errors</h3>
                    <div class="number">${errorTests}</div>
                </div>
                <div class="summary-card">
                    <h3>Pass Rate</h3>
                    <div class="number">${passRate}</div>
                </div>
            </div>
            
            <div class="database-info">
                <div class="db-card">
                    <h4>Source Database</h4>
                    <p>${sourceDatabase}</p>
                </div>
                <div class="db-card">
                    <h4>Target Database</h4>
                    <p>${targetDatabase}</p>
                </div>
            </div>
        </div>
        
        <div class="results-section">
            <h2>Test Results</h2>
            
            <#list results as result>
            <div class="result-item ${result.status?lower_case}">
                <div class="result-header" onclick="toggleResult(${result?counter})">
                    <span class="status-badge ${result.status?lower_case}">${result.status}</span>
                    <span class="query-name">${result.queryName!}</span>
                    <span class="row-counts">Source: ${result.sourceRowCount}, Target: ${result.targetRowCount}</span>
                    <span class="toggle-icon">▼</span>
                </div>
                <div class="result-content" id="result-${result?counter}">
                    <!-- SQL Query Display -->
                    <div class="sql-query-section">
                        <h4>SQL Query Executed</h4>
                        <div class="sql-query-container">
                            <pre class="sql-query"><code>${result.sqlQuery!}</code></pre>
                            <button class="copy-sql-btn" onclick="copySqlQuery(${result?counter})">
                                📋 Copy SQL
                            </button>
                        </div>
                    </div>
                    
                    <div class="details">
                        <p>${result.details!}</p>
                    </div>
                    
                    <!-- Enhanced Mismatch Data Table -->
                    <#if result.mismatchDetails?? && result.mismatchDetails?size gt 0>
                    <div class="mismatch-table-section">
                        <h4>Detailed Mismatch Data Table</h4>
                        <div class="table-container">
                            <table class="mismatch-data-table">
                                <thead>
                                    <tr>
                                        <th>Row #</th>
                                        <th>Column Name</th>
                                        <th>Source Data</th>
                                        <th>Target Data</th>
                                        <th>Difference</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <#list result.mismatchDetails as mismatch>
                                    <tr>
                                        <td class="row-number">${mismatch.rowNumber}</td>
                                        <td class="column-name">${mismatch.columnName}</td>
                                        <td class="source-data">${mismatch.sourceValue}</td>
                                        <td class="target-data">${mismatch.targetValue}</td>
                                        <td class="difference">
                                            <#if mismatch.difference??>
                                                ${mismatch.difference}
                                            <#else>
                                                <span class="diff-indicator">≠</span>
                                            </#if>
                                        </td>
                                        <td class="actions">
                                            <a href="#" 
                                               onclick="openDetailedComparison('${result.queryName?js_string}', ${mismatch.rowNumber}, '${mismatch.columnName?js_string}', '${mismatch.sourceValue?js_string}', '${mismatch.targetValue?js_string}', '${result.queryName?js_string}')"
                                               class="detail-link"
                                               title="View detailed comparison">
                                                🔍 Details
                                            </a>
                                            <a href="#" 
                                               onclick="openDataInvestigation('${result.queryName?js_string}', ${mismatch.rowNumber}, '${mismatch.columnName?js_string}')"
                                               class="investigate-link"
                                               title="Investigate data source">
                                                🔬 Investigate
                                            </a>
                                        </td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    </#if>
                    
                    <div class="query-section">
                        <h4>Query Details</h4>
                        <p><strong>Query Name:</strong> ${result.queryName!}</p>
                        <p><strong>Purpose:</strong> This query compares data between source and target databases for the specified customer.</p>
                        <p><strong>SQL Query:</strong> The actual SQL query executed is displayed above in the "SQL Query Executed" section.</p>
                    </div>
                </div>
            </div>
            </#list>
        </div>
    </div>
    
    <script>
        function toggleResult(index) {
            const content = document.getElementById('result-' + index);
            const icon = event.currentTarget.querySelector('.toggle-icon');
            
            if (content.style.display === 'none' || content.style.display === '') {
                content.style.display = 'block';
                icon.textContent = '▼';
            } else {
                content.style.display = 'none';
                icon.textContent = '▶';
            }
        }
        
        // Auto-expand failed and error results
        document.addEventListener('DOMContentLoaded', function() {
            const failedResults = document.querySelectorAll('.status-fail, .status-error');
            failedResults.forEach(function(result) {
                const index = result.closest('.result-item').querySelector('.result-header').getAttribute('onclick').match(/\d+/)[0];
                document.getElementById('result-' + index).classList.add('expanded');
            });
        });

        // Function to open detailed comparison in new tab
        function openDetailedComparison(queryName, rowNumber, columnName, sourceValue, targetValue, queryType) {
            const comparisonData = {
                queryName: queryName || 'Unknown Query',
                rowNumber: rowNumber || 'N/A',
                columnName: columnName || 'N/A',
                sourceValue: sourceValue || 'N/A',
                targetValue: targetValue || 'N/A',
                queryType: queryType || 'N/A',
                timestamp: Date.now(),
                databaseInfo: {
                    source: 'Source Database',
                    target: 'Target Database'
                }
            };
            
            const newTab = window.open('', '_blank');
            const htmlContent = '<!DOCTYPE html>' +
                '<html>' +
                '<head>' +
                    '<title>Detailed Comparison - ' + comparisonData.queryName + '</title>' +
                    '<style>' +
                        'body { font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }' +
                        '.container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }' +
                        '.header { background: linear-gradient(135deg, #6f42c1 0%, #e83e8c 100%); color: white; padding: 30px; text-align: center; }' +
                        '.header h1 { margin: 0; font-size: 28px; }' +
                        '.header p { margin: 10px 0 0 0; opacity: 0.9; }' +
                        '.content { padding: 30px; }' +
                        '.comparison-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin: 30px 0; }' +
                        '.comparison-card { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; }' +
                        '.comparison-card h3 { margin: 0 0 15px 0; color: #495057; }' +
                        '.data-value { font-family: "Courier New", monospace; background: white; padding: 15px; border-radius: 6px; border: 1px solid #dee2e6; font-size: 14px; }' +
                        '.metadata { background: #e9ecef; padding: 20px; border-radius: 6px; margin-top: 20px; }' +
                        '.metadata h4 { margin: 0 0 15px 0; color: #495057; }' +
                        '.metadata-item { display: flex; justify-content: space-between; margin-bottom: 10px; }' +
                        '.metadata-label { font-weight: 600; color: #6c757d; }' +
                        '.metadata-value { color: #495057; }' +
                    '</style>' +
                '</head>' +
                '<body>' +
                    '<div class="container">' +
                        '<div class="header">' +
                            '<h1>Detailed Data Comparison</h1>' +
                            '<p>Row-level data analysis for investigation</p>' +
                        '</div>' +
                        '<div class="content">' +
                            '<h2>Comparison Details</h2>' +
                            '<div class="comparison-grid">' +
                                '<div class="comparison-card">' +
                                    '<h3>Source Database (Expected)</h3>' +
                                    '<div class="data-value">' + comparisonData.sourceValue + '</div>' +
                                '</div>' +
                                '<div class="comparison-card">' +
                                    '<h3>Target Database (Actual)</h3>' +
                                    '<div class="data-value">' + comparisonData.targetValue + '</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="metadata">' +
                                '<h4>Investigation Metadata</h4>' +
                                '<div class="metadata-item">' +
                                    '<div class="metadata-label">Query Name</div>' +
                                    '<div class="metadata-value">' + comparisonData.queryName + '</div>' +
                                '</div>' +
                                '<div class="metadata-item">' +
                                    '<div class="metadata-label">Row Number</div>' +
                                    '<div class="metadata-value">' + comparisonData.rowNumber + '</div>' +
                                '</div>' +
                                '<div class="metadata-item">' +
                                    '<div class="metadata-label">Column Name</div>' +
                                    '<div class="metadata-value">' + comparisonData.columnName + '</div>' +
                                '</div>' +
                                '<div class="metadata-item">' +
                                    '<div class="metadata-label">Query Type</div>' +
                                    '<div class="metadata-value">' + comparisonData.queryType + '</div>' +
                                '</div>' +
                                '<div class="metadata-item">' +
                                    '<div class="metadata-label">Comparison Time</div>' +
                                    '<div class="metadata-value">' + comparisonData.timestamp + '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</body>' +
                '</html>';
            newTab.document.write(htmlContent);
            newTab.document.close();
        }
        
        // Function to copy SQL query to clipboard
        function copySqlQuery(resultIndex) {
            const sqlElement = document.querySelector('#result-' + resultIndex + ' .sql-query code');
            if (sqlElement) {
                const sqlText = sqlElement.textContent;
                navigator.clipboard.writeText(sqlText).then(() => {
                    // Show success feedback
                    const button = event.target;
                    const originalText = button.textContent;
                    button.textContent = '✅ Copied!';
                    button.style.background = '#28a745';
                    
                    setTimeout(() => {
                        button.textContent = originalText;
                        button.style.background = '#28a745';
                    }, 2000);
                }).catch(err => {
                    console.error('Failed to copy SQL query:', err);
                    // Fallback for older browsers
                    const textArea = document.createElement('textarea');
                    textArea.value = sqlText;
                    document.body.appendChild(textArea);
                    textArea.select();
                    document.execCommand('copy');
                    document.body.removeChild(textArea);
                    
                    const button = event.target;
                    const originalText = button.textContent;
                    button.textContent = '✅ Copied!';
                    button.style.background = '#28a745';
                    
                    setTimeout(() => {
                        button.textContent = originalText;
                        button.style.background = '#28a745';
                    }, 2000);
                });
            }
        }
        
        // Function to open data investigation in new tab
        function openDataInvestigation(queryName, rowNumber, columnName) {
            const investigationData = {
                queryName: queryName,
                rowNumber: rowNumber,
                columnName: columnName,
                timestamp: Date.now()
            };
            
            // Create a new tab with data investigation view
            const newTab = window.open('', '_blank');
            newTab.document.write(`
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Data Investigation - ' + queryName + '</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background: linear-gradient(135deg, #6f42c1 0%, #e83e8c 100%); color: white; padding: 30px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .header p { margin: 10px 0 0 0; opacity: 0.9; }
                        .content { padding: 30px; }
                        .investigation-section { background: #f8f9fa; border-radius: 8px; padding: 20px; margin: 20px 0; border-left: 4px solid #6f42c1; }
                        .investigation-section h3 { margin: 0 0 15px 0; color: #495057; }
                        .investigation-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 20px 0; }
                        .investigation-item { background: white; padding: 15px; border-radius: 6px; border: 1px solid #dee2e6; }
                        .investigation-label { font-weight: 600; color: #6c757d; font-size: 12px; text-transform: uppercase; margin-bottom: 8px; }
                        .investigation-value { color: #495057; font-family: 'Courier New', monospace; }
                        .suggestions { background: #e3f2fd; border: 1px solid #bbdefb; border-radius: 6px; padding: 20px; margin: 20px 0; }
                        .suggestions h3 { margin: 0 0 15px 0; color: #1976d2; }
                        .suggestion-list { list-style: none; padding: 0; }
                        .suggestion-list li { background: white; margin: 8px 0; padding: 12px; border-radius: 4px; border-left: 3px solid #2196f3; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔬 Data Investigation</h1>
                            <p>Query: ' + queryName + ' | Row: ' + rowNumber + ' | Column: ' + columnName + '</p>
                        </div>
                        <div class="content">
                            <div class="investigation-section">
                                <h3>📊 Investigation Details</h3>
                                <div class="investigation-grid">
                                    <div class="investigation-item">
                                        <div class="investigation-label">Query Name</div>
                                        <div class="investigation-value">' + queryName + '</div>
                                    </div>
                                    <div class="investigation-item">
                                        <div class="investigation-label">Row Number</div>
                                        <div class="investigation-value">' + rowNumber + '</div>
                                    </div>
                                    <div class="investigation-item">
                                        <div class="investigation-label">Column Name</div>
                                        <div class="investigation-value">' + columnName + '</div>
                                    </div>
                                    <div class="investigation-item">
                                        <div class="investigation-label">Investigation Time</div>
                                        <div class="investigation-value">' + Date.now() + '</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="suggestions">
                                <h3>💡 Investigation Suggestions</h3>
                                <ul class="suggestion-list">
                                    <li>🔍 Check data source timestamps and synchronization</li>
                                    <li>📊 Verify data transformation rules and mappings</li>
                                    <li>🔄 Review data migration scripts and procedures</li>
                                    <li>📋 Compare data validation rules between environments</li>
                                    <li>⚡ Check for real-time data updates or caching</li>
                                    <li>🔧 Verify database constraints and triggers</li>
                                    <li>📈 Analyze data growth patterns and archiving</li>
                                    <li>🛡️ Review data security and access controls</li>
                                </ul>
                            </div>
                            
                            <div class="investigation-section">
                                <h3>📝 Next Steps</h3>
                                <div class="investigation-grid">
                                    <div class="investigation-item">
                                        <div class="investigation-label">Immediate Actions</div>
                                        <div class="investigation-value">• Review data source<br>• Check migration logs<br>• Validate transformation rules</div>
                                    </div>
                                    <div class="investigation-item">
                                        <div class="investigation-label">Long-term Solutions</div>
                                        <div class="investigation-value">• Implement data validation<br>• Set up monitoring<br>• Document procedures</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
            `);
            newTab.document.close();
        }
    </script>
</body>
</html> 