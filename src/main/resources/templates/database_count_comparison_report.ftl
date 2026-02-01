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
            color: #333;
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
            font-size: 1.1em;
        }
        .content {
            padding: 30px;
        }
        .database-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 30px;
        }
        .db-card {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
        }
        .db-card h3 {
            margin: 0 0 15px 0;
            color: #495057;
            font-size: 1.2em;
        }
        .db-card p {
            margin: 5px 0;
            color: #6c757d;
        }
        .summary {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
        }
        .summary h3 {
            margin: 0 0 15px 0;
            color: #1976d2;
        }
        .summary-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .stat-item {
            text-align: center;
            padding: 15px;
            background: white;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
        }
        .stat-number {
            font-size: 2em;
            font-weight: bold;
            color: #1976d2;
        }
        .stat-label {
            color: #666;
            font-size: 0.9em;
            margin-top: 5px;
        }
        .customer-section {
            margin-bottom: 40px;
        }
        .customer-header {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 15px 20px;
            margin-bottom: 20px;
        }
        .customer-header h3 {
            margin: 0;
            color: #495057;
        }
        .customer-id {
            color: #6c757d;
            font-size: 0.9em;
            margin-top: 5px;
        }
        .results-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .results-table th {
            background: #f8f9fa;
            padding: 12px;
            text-align: left;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
            color: #495057;
        }
        .results-table td {
            padding: 12px;
            border-bottom: 1px solid #e9ecef;
        }
        .results-table tr:hover {
            background: #f8f9fa;
        }
        .status-match {
            color: #28a745;
            font-weight: 600;
        }
        .status-mismatch {
            color: #dc3545;
            font-weight: 600;
        }
        .status-error {
            color: #ffc107;
            font-weight: 600;
        }
        .count-cell {
            text-align: right;
            font-family: 'Courier New', monospace;
        }
        .diff-cell {
            text-align: right;
            font-family: 'Courier New', monospace;
            font-weight: 600;
        }
        .diff-positive {
            color: #28a745;
        }
        .diff-negative {
            color: #dc3545;
        }
        .diff-zero {
            color: #6c757d;
        }
        .footer {
            background: #f8f9fa;
            border-top: 1px solid #e9ecef;
            padding: 20px;
            text-align: center;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>${reportTitle}</h1>
            <p>Generated on ${generatedAt}</p>
        </div>
        
        <div class="content">
            <div class="database-info">
                <div class="db-card">
                    <h3>Source Database (NA)</h3>
                    <p>${sourceDatabase}</p>
                </div>
                <div class="db-card">
                    <h3>Target Database (EU)</h3>
                    <p>${targetDatabase}</p>
                </div>
            </div>
            
            <div class="summary">
                <h3>Summary Statistics</h3>
                <div class="summary-stats">
                    <div class="stat-item">
                        <div class="stat-number">${totalQueries}</div>
                        <div class="stat-label">Total Queries</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #28a745;">${matches}</div>
                        <div class="stat-label">Matches</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #dc3545;">${mismatches}</div>
                        <div class="stat-label">Mismatches</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #ffc107;">${errors}</div>
                        <div class="stat-label">Errors</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${matchRate}</div>
                        <div class="stat-label">Match Rate</div>
                    </div>
                </div>
            </div>
            
            <#list resultsByCustomer?keys as customerId>
            <div class="customer-section">
                <div class="customer-header">
                    <h3>Customer: ${customerId}</h3>
                </div>
                
                <table class="results-table">
                    <thead>
                        <tr>
                            <th>Query #</th>
                            <th>Table Name</th>
                            <th class="count-cell">NA Count</th>
                            <th class="count-cell">EU Count</th>
                            <th class="diff-cell">Difference</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list resultsByCustomer[customerId] as result>
                        <tr>
                            <td>${result.queryNumber}</td>
                            <td>${result.tableName}</td>
                            <td class="count-cell">${result.sourceCount!0}</td>
                            <td class="count-cell">${result.targetCount!0}</td>
                            <td class="diff-cell">
                                <#if result.error??>
                                    <span class="status-error">N/A</span>
                                <#elseif result.difference??>
                                    <#if result.difference gt 0>
                                        <span class="diff-positive">+${result.difference}</span>
                                    <#elseif result.difference lt 0>
                                        <span class="diff-negative">${result.difference}</span>
                                    <#else>
                                        <span class="diff-zero">0</span>
                                    </#if>
                                <#else>
                                    N/A
                                </#if>
                            </td>
                            <td>
                                <#if result.error??>
                                    <span class="status-error">❌ Error</span>
                                <#elseif result.matches>
                                    <span class="status-match">✅ Match</span>
                                <#else>
                                    <span class="status-mismatch">⚠️ Mismatch</span>
                                </#if>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
            </#list>
        </div>
        
        <div class="footer">
            <p>EU Data Migration Automation - Database Count Comparison Report</p>
        </div>
    </div>
</body>
</html>

