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
            max-width: 1400px;
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
        .customer-info {
            background: #e3f2fd;
            padding: 20px;
            border-bottom: 1px solid #bbdefb;
            text-align: center;
        }
        .customer-info h2 {
            margin: 0 0 10px 0;
            color: #1976d2;
        }
        .customer-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 15px;
        }
        .customer-detail {
            background: white;
            padding: 15px;
            border-radius: 6px;
            border-left: 4px solid #2196f3;
        }
        .customer-detail h4 {
            margin: 0 0 8px 0;
            color: #495057;
            font-size: 0.9em;
        }
        .customer-detail p {
            margin: 0;
            font-weight: 600;
            color: #1976d2;
        }
        
        /* Count Validation Section (First Section) */
        .count-validation-section {
            padding: 30px;
            border-bottom: 2px solid #dee2e6;
        }
        .count-validation-section h2 {
            color: #333;
            margin-bottom: 25px;
            border-bottom: 3px solid #28a745;
            padding-bottom: 10px;
            display: flex;
            align-items: center;
        }
        .count-validation-section h2::before {
            content: "📊";
            margin-right: 10px;
            font-size: 1.2em;
        }
        .count-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .count-summary-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            border-left: 4px solid #007bff;
        }
        .count-summary-card.passed {
            border-left-color: #28a745;
        }
        .count-summary-card.failed {
            border-left-color: #dc3545;
        }
        .count-summary-card.error {
            border-left-color: #ffc107;
        }
        .count-summary-card h3 {
            margin: 0 0 10px 0;
            color: #333;
            font-size: 1.1em;
        }
        .count-summary-card .number {
            font-size: 2em;
            font-weight: bold;
            color: #007bff;
        }
        .count-summary-card.passed .number {
            color: #28a745;
        }
        .count-summary-card.failed .number {
            color: #dc3545;
        }
        .count-summary-card.error .number {
            color: #ffc107;
        }
        
        /* Count Validation Table */
        .count-validation-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .count-validation-table th {
            background: #f8f9fa;
            padding: 15px 12px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid #dee2e6;
        }
        .count-validation-table td {
            padding: 12px;
            border-bottom: 1px solid #dee2e6;
            vertical-align: middle;
        }
        .count-validation-table tr:hover {
            background-color: #f8f9fa;
        }
        .query-name {
            font-weight: 600;
            color: #495057;
        }
        .count-cell {
            text-align: center;
            font-weight: 600;
        }
        .source-count {
            color: #28a745;
        }
        .target-count {
            color: #dc3545;
        }
        .status-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85em;
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
        .mismatch-link {
            display: inline-block;
            padding: 6px 12px;
            background: #6f42c1;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 0.85em;
            font-weight: 500;
            transition: all 0.2s ease;
        }
        .mismatch-link:hover {
            background: #5a32a3;
            transform: translateY(-1px);
        }
        .no-mismatch {
            color: #6c757d;
            font-style: italic;
        }
        
        /* Detailed Results Section */
        .detailed-results-section {
            padding: 30px;
        }
        .detailed-results-section h2 {
            color: #333;
            margin-bottom: 25px;
            border-bottom: 3px solid #6f42c1;
            padding-bottom: 10px;
            display: flex;
            align-items: center;
        }
        .detailed-results-section h2::before {
            content: "🔍";
            margin-right: 10px;
            font-size: 1.2em;
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
        
        /* Analysis Section */
        .analysis-section {
            background: #e8f5e8;
            border: 1px solid #c3e6c3;
            border-radius: 8px;
            padding: 25px;
            margin: 30px 0;
        }
        .analysis-section h3 {
            margin: 0 0 20px 0;
            color: #155724;
            display: flex;
            align-items: center;
        }
        .analysis-section h3::before {
            content: "📈";
            margin-right: 10px;
        }
        .analysis-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        .analysis-item {
            background: white;
            padding: 20px;
            border-radius: 6px;
            border-left: 4px solid #28a745;
        }
        .analysis-item h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
        .analysis-item p {
            margin: 0;
            color: #6c757d;
            line-height: 1.5;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>${reportTitle}</h1>
            <p>Generated on ${generatedAt}</p>
        </div>
        
        <!-- Customer Information -->
        <div class="customer-info">
            <h2>Customer Analysis: ${customerName}</h2>
            <div class="customer-details">
                <div class="customer-detail">
                    <h4>Customer ID</h4>
                    <p>${customerId!}</p>
                </div>
                <div class="customer-detail">
                    <h4>Source Database</h4>
                    <p>${sourceDatabase}</p>
                </div>
                <div class="customer-detail">
                    <h4>Target Database</h4>
                    <p>${targetDatabase}</p>
                </div>
            </div>
        </div>
        
        <!-- Count Validation Section (First Section) -->
        <div class="count-validation-section">
            <h2>Count Validation Summary</h2>
            
            <!-- Summary Cards -->
            <div class="count-summary">
                <div class="count-summary-card">
                    <h3>Total Tests</h3>
                    <div class="number">${totalTests}</div>
                </div>
                <div class="count-summary-card passed">
                    <h3>Passed</h3>
                    <div class="number">${passedTests}</div>
                </div>
                <div class="count-summary-card failed">
                    <h3>Failed</h3>
                    <div class="number">${failedTests}</div>
                </div>
                <div class="count-summary-card error">
                    <h3>Errors</h3>
                    <div class="number">${errorTests}</div>
                </div>
                <div class="count-summary-card">
                    <h3>Pass Rate</h3>
                    <div class="number">${passRate}</div>
                </div>
            </div>
            
            <!-- Count Validation Table -->
            <table class="count-validation-table">
                <thead>
                    <tr>
                        <th>Query Name</th>
                        <th>Source Count</th>
                        <th>Target Count</th>
                        <th>Status</th>
                        <th>Mismatches</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <#list countValidations?values as validation>
                    <tr>
                        <td class="query-name">${validation.queryName}</td>
                        <td class="count-cell source-count">${validation.sourceCount}</td>
                        <td class="count-cell target-count">${validation.targetCount}</td>
                        <td class="count-cell">
                            <span class="status-badge status-${validation.status?lower_case}">${validation.status}</span>
                        </td>
                        <td class="count-cell">
                            <#if (validation.mismatchCount > 0)>
                                <a href="#" 
                                   onclick="openMismatchDetails('${validation.queryName?js_string}', ${validation.mismatchCount}, ${validation.detailCount})"
                                   class="mismatch-link">
                                    ${validation.mismatchCount} Records Mismatching for ${validation.detailCount} Columns
                                </a>
                            <#else>
                                <span class="no-mismatch">No mismatches</span>
                            </#if>
                        </td>
                        <td class="count-cell">
                            <#if (validation.mismatchCount > 0)>
                                <a href="#" 
                                   onclick="openDetailedAnalysis('${validation.queryName?js_string}')"
                                   class="detail-link">
                                    🔍 Analyze
                                </a>
                            </#if>
                        </td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        </div>
        
        <!-- Analysis Section -->
        <div class="analysis-section">
            <h3>Analysis Summary</h3>
            <div class="analysis-grid">
                <div class="analysis-item">
                    <h4>Data Consistency</h4>
                    <p>
                        <#if (passedTests == totalTests)>
                            ✅ All queries passed validation. Data is consistent between source and target databases.
                        <#elseif (failedTests > 0)>
                            ⚠️ ${failedTests} queries failed validation. Data inconsistencies detected that require investigation.
                        <#else>
                            ℹ️ Analysis completed with ${errorTests} errors. Please review error details.
                        </#if>
                    </p>
                </div>
                <div class="analysis-item">
                    <h4>Migration Status</h4>
                    <p>
                        <#if (passedTests == totalTests)>
                            🎯 Migration appears successful. All customer association data has been properly migrated.
                        <#else>
                            🔄 Migration requires attention. Some data may not have been properly migrated or synchronized.
                        </#if>
                    </p>
                </div>
                <div class="analysis-item">
                    <h4>Next Steps</h4>
                    <p>
                        <#if (failedTests > 0)>
                            📋 Review failed queries and investigate data mismatches. Click on mismatch links for detailed analysis.
                        <#else>
                            ✅ No immediate action required. Data migration appears successful.
                        </#if>
                    </p>
                </div>
            </div>
        </div>
        
        <!-- Detailed Results Section -->
        <div class="detailed-results-section">
            <h2>Detailed Query Results</h2>
            
            <#list queryResults as result>
            <div class="result-item ${result.status?lower_case}">
                <div class="result-header" onclick="toggleResult(${result?counter})">
                    <span class="result-title">${result.queryName!}</span>
                    <span class="result-status status-${result.status?lower_case}">${result.status}</span>
                    <span class="toggle-icon">▼</span>
                </div>
                <div class="result-content" id="result-${result?counter}">
                    <div class="row-count-comparison">
                        <div class="count-card">
                            <h4>Source Database</h4>
                            <div class="count-number">${result.sourceRowCount}</div>
                        </div>
                        <div class="count-card">
                            <h4>Target Database</h4>
                            <div class="count-number">${result.targetRowCount}</div>
                        </div>
                    </div>
                    
                    <div class="details">
                        <p>${result.details!}</p>
                    </div>
                    
                    <!-- Mismatch Data Table -->
                    <#if result.mismatchDetails?? && result.mismatchDetails?size gt 0>
                    <div class="mismatch-table-section">
                        <h4>Detailed Mismatch Data</h4>
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
                                           onclick="openDetailedComparison('${result.queryName?js_string}', ${mismatch.rowNumber}, '${mismatch.columnName?js_string}', '${mismatch.sourceValue?js_string}', '${mismatch.targetValue?js_string}')"
                                           class="detail-link">
                                            🔍 Details
                                        </a>
                                    </td>
                                </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                    </#if>
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
        
        // Function to open mismatch details in new tab
        function openMismatchDetails(queryName, mismatchCount, detailCount) {
            const newTab = window.open('', '_blank');
            const htmlContent = `
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Mismatch Details - ' + queryName + '</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); color: white; padding: 30px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 30px; }
                        .mismatch-summary { background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 6px; padding: 20px; margin: 20px 0; }
                        .mismatch-summary h3 { margin: 0 0 15px 0; color: #721c24; }
                        .mismatch-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
                        .mismatch-item { background: white; padding: 15px; border-radius: 6px; border-left: 4px solid #dc3545; }
                        .mismatch-item h4 { margin: 0 0 8px 0; color: #495057; }
                        .mismatch-item p { margin: 0; font-weight: 600; color: #dc3545; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>⚠️ Mismatch Details</h1>
                            <p>Query: ' + queryName + '</p>
                        </div>
                        <div class="content">
                            <div class="mismatch-summary">
                                <h3>Mismatch Summary</h3>
                                <div class="mismatch-grid">
                                    <div class="mismatch-item">
                                        <h4>Total Mismatched Records</h4>
                                        <p>' + mismatchCount + '</p>
                                    </div>
                                    <div class="mismatch-item">
                                        <h4>Columns with Differences</h4>
                                        <p>' + detailCount + '</p>
                                    </div>
                                    <div class="mismatch-item">
                                        <h4>Query Name</h4>
                                        <p>' + queryName + '</p>
                                    </div>
                                </div>
                            </div>
                            <p><strong>Note:</strong> This view shows the summary of mismatches. For detailed row-by-row analysis, please refer to the main report.</p>
                        </div>
                    </div>
                </body>
                </html>
            `;
            newTab.document.write(htmlContent);
            newTab.document.close();
        }
        
        // Function to open detailed analysis
        function openDetailedAnalysis(queryName) {
            const newTab = window.open('', '_blank');
            const htmlContent = `
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Detailed Analysis - ${r"${queryName}"}</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background: linear-gradient(135deg, #6f42c1 0%, #e83e8c 100%); color: white; padding: 30px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 30px; }
                        .analysis-section { background: #f8f9fa; border-radius: 8px; padding: 20px; margin: 20px 0; }
                        .analysis-section h3 { margin: 0 0 15px 0; color: #495057; }
                        .suggestions { background: #e3f2fd; border: 1px solid #bbdefb; border-radius: 6px; padding: 20px; margin: 20px 0; }
                        .suggestions h3 { margin: 0 0 15px 0; color: #1976d2; }
                        .suggestion-list { list-style: none; padding: 0; }
                        .suggestion-list li { background: white; margin: 8px 0; padding: 12px; border-radius: 4px; border-left: 3px solid #2196f3; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔍 Detailed Analysis</h1>
                            <p>Query: ' + queryName + '</p>
                        </div>
                        <div class="content">
                            <div class="analysis-section">
                                <h3>Analysis Details</h3>
                                <p>This query has data mismatches that require investigation. The detailed analysis shows specific differences between source and target databases.</p>
                            </div>
                            <div class="suggestions">
                                <h3>Investigation Suggestions</h3>
                                <ul class="suggestion-list">
                                    <li>🔍 Check data source timestamps and synchronization</li>
                                    <li>📊 Verify data transformation rules and mappings</li>
                                    <li>🔄 Review data migration scripts and procedures</li>
                                    <li>📋 Compare data validation rules between environments</li>
                                    <li>⚡ Check for real-time data updates or caching</li>
                                    <li>🔧 Verify database constraints and triggers</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
            `;
            newTab.document.write(htmlContent);
            newTab.document.close();
        }
        
        // Function to open detailed comparison
        function openDetailedComparison(queryName, rowNumber, columnName, sourceValue, targetValue) {
            const newTab = window.open('', '_blank');
            const htmlContent = `
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Detailed Comparison - ${r"${queryName}"}</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }
                        .container { max-width: 1000px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background: linear-gradient(135deg, #6f42c1 0%, #e83e8c 100%); color: white; padding: 30px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content { padding: 30px; }
                        .comparison-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin: 30px 0; }
                        .comparison-card { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 20px; }
                        .comparison-card h3 { margin: 0 0 15px 0; color: #495057; }
                        .data-value { font-family: 'Courier New', monospace; background: white; padding: 15px; border-radius: 6px; border: 1px solid #dee2e6; font-size: 14px; }
                        .metadata { background: #e9ecef; padding: 20px; border-radius: 6px; margin-top: 20px; }
                        .metadata h4 { margin: 0 0 15px 0; color: #495057; }
                        .metadata-item { display: flex; justify-content: space-between; margin-bottom: 10px; }
                        .metadata-label { font-weight: 600; color: #6c757d; }
                        .metadata-value { color: #495057; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Detailed Data Comparison</h1>
                            <p>Row-level data analysis for investigation</p>
                        </div>
                        <div class="content">
                            <h2>Comparison Details</h2>
                            <div class="comparison-grid">
                                <div class="comparison-card">
                                    <h3>Source Database (Expected)</h3>
                                    <div class="data-value">${r"${sourceValue}"}</div>
                                </div>
                                <div class="comparison-card">
                                    <h3>Target Database (Actual)</h3>
                                    <div class="data-value">${r"${targetValue}"}</div>
                                </div>
                            </div>
                            <div class="metadata">
                                <h4>Investigation Metadata</h4>
                                <div class="metadata-item">
                                    <div class="metadata-label">Query Name</div>
                                    <div class="metadata-value">${r"${queryName}"}</div>
                                </div>
                                <div class="metadata-item">
                                    <div class="metadata-label">Row Number</div>
                                    <div class="metadata-value">${r"${rowNumber}"}</div>
                                </div>
                                <div class="metadata-item">
                                    <div class="metadata-label">Column Name</div>
                                    <div class="metadata-value">${r"${columnName}"}</div>
                                </div>
                                <div class="metadata-item">
                                    <div class="metadata-label">Comparison Time</div>
                                    <div class="metadata-value">${.now?string("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
            `;
            newTab.document.write(htmlContent);
            newTab.document.close();
        }
    </script>
</body>
</html>
